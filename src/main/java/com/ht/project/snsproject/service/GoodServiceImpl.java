package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.GoodMapper;
import com.ht.project.snsproject.model.good.GoodListParam;
import com.ht.project.snsproject.model.good.GoodStatusParam;
import com.ht.project.snsproject.model.good.GoodUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GoodServiceImpl implements GoodService {


  @Resource(name = "cacheRedisTemplate")
  private ValueOperations<String, Object> valueOps;

  @Autowired
  private GoodMapper goodMapper;

  @Autowired
  private RedisCacheService redisCacheService;

  @Autowired
  @Qualifier("cacheRedisTemplate")
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private GoodService goodService;

  @Transactional(readOnly = true)
  @Cacheable(value = "goodPushed", key = "'goodPushed:' + #feedId + ':' + #userId")
  @Override
  public boolean isGoodPushed(int feedId, String userId) {

    return goodMapper.getGoodPushedStatus(new GoodStatusParam(feedId, userId));
  }

  @Override
  public List<GoodUser> getGoodList(int feedId, Integer cursor) {

    return goodMapper.getGoodList(GoodListParam.builder()
            .feedId(feedId)
            .cursor(cursor)
            .build());
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "good", key = "'good:' + #feedId")
  @Override
  public int getGood(int feedId) {

    return goodMapper.getGood(feedId);
  }

  @Transactional
  @Override
  public void addGood(int feedId, String userId) {

    goodService.getGood(feedId);

    addGoodToCache(feedId, userId, goodService.isGoodPushed(feedId, userId));
    increaseGoodCount(feedId);
  }

  @Transactional
  private void addGoodToCache(int feedId, String userId, boolean goodPushed) {

    String goodPushedKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD_PUSHED, feedId, userId);

    if (!goodPushed) {
      valueOps.set(goodPushedKey, true, 60L, TimeUnit.SECONDS);

    } else {
      throw new DuplicateRequestException("중복된 요청입니다.");
    }
  }


  @Transactional
  private void increaseGoodCount(int feedId) {

    String goodKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    valueOps.increment(goodKey);
  }

  @Transactional
  @Override
  public void cancelGood(int feedId, String userId) {

    goodService.getGood(feedId);

    cancelGoodInCache(feedId, userId, goodService.isGoodPushed(feedId, userId));
    decreaseGoodCount(feedId);
  }

  @Transactional
  private void cancelGoodInCache(int feedId, String userId, boolean goodPushed) {

    String goodPushedKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD_PUSHED, feedId, userId);

    if (!goodPushed) {
      throw new InvalidApproachException("비정상적인 요청입니다.");
    } else {
      valueOps.set(goodPushedKey, false, 60L, TimeUnit.SECONDS);
    }
  }

  @Transactional
  private void decreaseGoodCount(int feedId) {

    String goodKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    valueOps.decrement(goodKey);
  }
}
