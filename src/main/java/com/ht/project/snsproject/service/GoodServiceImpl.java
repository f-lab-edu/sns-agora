package com.ht.project.snsproject.service;

import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.GoodMapper;
import com.ht.project.snsproject.model.good.GoodUserDelete;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
  public int getGood(int feedId) {

    String key = "good:"+feedId;

    Integer good = (Integer) redisTemplate.boundValueOps(key).get();

    if(good==null){
      good = goodMapper.getGood(feedId);
      redisTemplate.boundValueOps(key).set(good,2L,TimeUnit.HOURS);
    }

    return good;
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

    List<String> goodList = new ArrayList<>();
    if(goodMapper.hasFeedId(feedId)!=0){
      goodList = goodMapper.getGoodList(feedId);
    }

    return goodList;
  }

  @Transactional
  @Override
  public void addGood(int feedId, String userId) {

    feedService.getFeedInfoCache(feedId);
    getGood(feedId);

    List<String> goodList = getGoodList(feedId);

    if(goodList != null) {
      if (goodList.contains(userId)) {
        throw new DuplicateRequestException("중복된 요청입니다.");
      }
    }

    String goodListKey = "goodList:" + feedId;
    String goodKey = "good:" + feedId;

    redisTemplate.opsForList().rightPush(goodListKey, userId);
    redisTemplate.opsForValue().increment(goodKey);

    long ttl = redisTemplate.getExpire(goodKey);

    /*
      캐시 시간이 1시간 미만일 때,
      좋아요 요청이 들어오면 캐시 시간을 1시간 증가시킴으로써
      스케줄러의 공백이 없도록한다.
     */
    if(ttl < 60 * 60){
      redisTemplate.expire("feedInfo:" + feedId,1L, TimeUnit.HOURS);
      redisTemplate.expire(goodListKey,1L,TimeUnit.HOURS);
      redisTemplate.expire(goodKey,1L, TimeUnit.HOURS);
    }
  }

  @Transactional
  @Override
  public void cancelGood(int feedId, String userId) {

    String key = "good:" + feedId;
    Integer good = (Integer) redisTemplate.opsForValue().get(key);
    GoodUserDelete goodUserDelete = new GoodUserDelete(feedId, userId);
    if (good != null) {
      if (good == 0) {
        throw new InvalidApproachException("비정상적인 요청입니다.");
      }
      Long userDeleteResult = redisTemplate.opsForList().remove("goodList:" + feedId,1, userId);
      if(userDeleteResult > 0) {
        redisTemplate.opsForValue().decrement(key);
      }

      if (goodMapper.deleteGoodUser(goodUserDelete)) {
        goodMapper.decrementGood(feedId);
      } else if(userDeleteResult == 0){

        throw new InvalidApproachException("비정상적인 요청입니다.");
      }


    } else {

      boolean deleteUserResult = goodMapper.deleteGoodUser(goodUserDelete);

      if (!deleteUserResult) {
        throw new InvalidApproachException("비정상적인 요청입니다.");
      }

      boolean decrementGoodResult = goodMapper.decrementGood(feedId);

      if (!decrementGoodResult) {
        throw new InvalidApproachException("비정상적인 요청입니다.");
      }
    }
  }
}
