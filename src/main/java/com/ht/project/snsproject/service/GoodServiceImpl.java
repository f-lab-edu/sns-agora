package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.GoodMapper;
import com.ht.project.snsproject.model.good.GoodListParam;
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

  public GoodServiceImpl(GoodMapper goodMapper,
                         RedisCacheService redisCacheService,
                         @Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                         GoodRepository goodRepository) {
    this.goodMapper = goodMapper;
    this.redisCacheService = redisCacheService;
    this.redisTemplate = redisTemplate;
    this.goodRepository = goodRepository;
  }

  private final GoodMapper goodMapper;

  private final RedisCacheService redisCacheService;

  private final RedisTemplate<String, Object> redisTemplate;

  private final GoodRepository goodRepository;


  @Override
  public List<GoodUser> getGoodList(int feedId, Integer cursor) {

    return goodMapper.getGoodList(GoodListParam.builder()
            .feedId(feedId)
            .cursor(cursor)
            .build());
  }


  @Transactional
  @Override
  public void addGood(int feedId, String userId) {

    goodRepository.getGood(feedId);

    addGoodToCache(feedId, userId, goodRepository.isGoodPushed(feedId, userId));
    increaseGoodCount(feedId);
  }

  @Transactional
  private void addGoodToCache(int feedId, String userId, boolean goodPushed) {

    String goodPushedKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD_PUSHED, feedId, userId);

    if (!goodPushed) {
      redisTemplate.opsForValue().set(goodPushedKey, true, 60L, TimeUnit.SECONDS);

    } else {
      throw new DuplicateRequestException("중복된 요청입니다.");
    }
  }


  @Transactional
  private void increaseGoodCount(int feedId) {

    String goodKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    redisTemplate.opsForValue().increment(goodKey);
  }

  @Transactional
  @Override
  public void cancelGood(int feedId, String userId) {

    goodRepository.getGood(feedId);

    cancelGoodInCache(feedId, userId, goodRepository.isGoodPushed(feedId, userId));
    decreaseGoodCount(feedId);
  }

  @Transactional
  private void cancelGoodInCache(int feedId, String userId, boolean goodPushed) {

    String goodPushedKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD_PUSHED, feedId, userId);

    if (!goodPushed) {
      throw new InvalidApproachException("비정상적인 요청입니다.");
    } else {
      redisTemplate.opsForValue().set(goodPushedKey, false, 60L, TimeUnit.SECONDS);
    }
  }

  @Transactional
  private void decreaseGoodCount(int feedId) {

    String goodKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    redisTemplate.opsForValue().decrement(goodKey);
  }
}
