package com.ht.project.snsproject.service;

import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.GoodMapper;
import com.ht.project.snsproject.model.good.GoodUserDelete;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoodServiceImpl implements GoodService {

  @Autowired
  GoodMapper goodMapper;

  @Autowired
  FeedService feedService;

  @Autowired
  RedisTemplate<String,Object> redisTemplate;

  @Override
  @Cacheable(value = "feeds", key = "'Good:'+#feedId")
  public int getGood(int feedId) {

    return goodMapper.getGood(feedId);
  }

  @Override
  public List<String> getGoodList(int feedId) {

    String key = "goodList:" + feedId;

    if (redisTemplate.hasKey(key)) {

      List<Object> cache = redisTemplate.opsForList().range(key,0,-1);

      return cache.stream()
              .map(object -> Objects.toString(object, null))
              .collect(Collectors.toList());
    }

    return goodMapper.getGoodList(feedId);
  }

  @Transactional
  @Override
  public void increaseGood(int feedId, String userId) {

    feedService.getFeedInfoCache(feedId);
    getGood(feedId);
    List<String> goodList = getGoodList(feedId);

    if (goodList.contains(userId)) {
      throw new DuplicateRequestException("중복된 요청입니다.");
    }

    String goodListKey = "goodList:" + feedId;


    redisTemplate.expire("feedInfo:" + feedId,5L, TimeUnit.HOURS);
    redisTemplate.opsForList().rightPush(goodListKey, userId);

    String goodKey = "good:" + feedId;
    redisTemplate.expire(goodKey,5L, TimeUnit.HOURS);
    redisTemplate.opsForValue().increment(goodKey);
    redisTemplate.expire(goodKey,5L,TimeUnit.HOURS);
  }

  @Transactional
  @Override
  public void cancelGood(int feedId, String userId) {

    String key = "good:" + feedId;
    Integer good = (Integer) redisTemplate.opsForValue().get(key);

    if (good != null) {
      if (good == 0) {
        throw new InvalidApproachException("비정상적인 요청입니다.");
      }
      redisTemplate.opsForValue().decrement(key);
      redisTemplate.opsForList().remove("goodList:" + feedId,1, userId);
    } else {

      boolean deleteUserResult = goodMapper.deleteGoodUser(
              new GoodUserDelete(feedId, userId));

      if (!deleteUserResult) {
        throw new InvalidApproachException("비정상적인 요청입니다.");
      }

      boolean decrementGoodResult = goodMapper.decrementGood(feedId);

      if (!decrementGoodResult) {
        throw new InvalidApproachException("비정상적인 요청입니다.");
      }
    }
  }

  /*
   * 스케줄러 수정 후 추가 예정.
   */

}
