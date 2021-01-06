package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.model.good.GoodUser;
import com.ht.project.snsproject.repository.good.GoodRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GoodServiceImpl implements GoodService {

  public GoodServiceImpl(RedisCacheService redisCacheService,
                         @Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                         GoodRepository goodRepository) {
    this.redisCacheService = redisCacheService;
    this.redisTemplate = redisTemplate;
    this.goodRepository = goodRepository;
  }

  private final RedisCacheService redisCacheService;

  private final RedisTemplate<String, Object> redisTemplate;

  private final GoodRepository goodRepository;


  @Transactional(readOnly = true)
  @Override
  public List<GoodUser> getGoodList(int feedId, Integer cursor) {

    return goodRepository.findGoodPushUserList(feedId, cursor);
  }


  @Transactional
  @Override
  public void addGood(int feedId, String userId) {

    addGoodToCache(feedId, userId, goodRepository.isGoodPushed(feedId, userId));
    increaseGoodCount(feedId);
  }

  private void addGoodToCache(int feedId, String userId, boolean goodPushed) {

    String goodPushedKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD_PUSHED, feedId, userId);

    if (!goodPushed) {
      redisTemplate.opsForValue().set(goodPushedKey, true, 60L, TimeUnit.SECONDS);

    } else {
      throw new DuplicateRequestException("중복된 요청입니다.");
    }
  }

  private void increaseGoodCount(int feedId) {

    goodRepository.getGood(feedId);
    String goodKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    redisTemplate.opsForValue().increment(goodKey);
  }

  @Transactional
  @Override
  public void cancelGood(int feedId, String userId) {

    cancelGoodInCache(feedId, userId, goodRepository.isGoodPushed(feedId, userId));
    decreaseGoodCount(feedId);
  }

  private void cancelGoodInCache(int feedId, String userId, boolean goodPushed) {

    String goodPushedKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD_PUSHED, feedId, userId);

    if (!goodPushed) {
      throw new InvalidApproachException("비정상적인 요청입니다.");
    } else {
      redisTemplate.opsForValue().set(goodPushedKey, false, 60L, TimeUnit.SECONDS);
    }
  }

  private void decreaseGoodCount(int feedId) {

    goodRepository.getGood(feedId);
    String goodKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    redisTemplate.opsForValue().decrement(goodKey);
  }
}
