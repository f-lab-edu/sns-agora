package com.ht.project.snsproject.service;

import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.RecommendMapper;
import com.ht.project.snsproject.model.recommend.RecommendUserDelete;
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
public class RecommendServiceImpl implements RecommendService {

  @Autowired
  RecommendMapper recommendMapper;

  @Autowired
  FeedService feedService;

  @Autowired
  RedisTemplate<String,Object> redisTemplate;

  @Override
  @Cacheable(value = "feeds", key = "'recommend:'+#feedId")
  public int getRecommend(int feedId) {

    return recommendMapper.getRecommend(feedId);
  }

  @Override
  public List<String> getRecommendList(int feedId) {

    String key = "recommendList:" + feedId;

    if (redisTemplate.hasKey(key)) {

      List<Object> cache = redisTemplate.opsForList().range(key,0,-1);

      return cache.stream()
              .map(object -> Objects.toString(object, null))
              .collect(Collectors.toList());
    }

    return recommendMapper.getRecommendList(feedId);
  }

  @Transactional
  @Override
  public void increaseRecommend(int feedId, String userId) {

    feedService.getFeedInfoCache(feedId);
    getRecommend(feedId);
    List<String> recommendList = getRecommendList(feedId);

    if (recommendList.contains(userId)) {
      throw new DuplicateRequestException("중복된 요청입니다.");
    }

    String recommendListKey = "recommendList:" + feedId;


    redisTemplate.expire("feedInfo:" + feedId,5L, TimeUnit.HOURS);
    redisTemplate.opsForList().rightPush(recommendListKey, userId);
    redisTemplate.expire(recommendListKey,5L, TimeUnit.HOURS);

    String recommendKey = "recommend:" + feedId;
    redisTemplate.opsForValue().increment(recommendKey);
    redisTemplate.expire(recommendKey,5L,TimeUnit.HOURS);
  }

  @Transactional
  @Override
  public void cancelRecommend(int feedId, String userId) {

    String key = "recommend:" + feedId;
    Integer recommend = (Integer) redisTemplate.opsForValue().get(key);

    if (recommend != null) {
      if (recommend == 0) {
        throw new InvalidApproachException("비정상적인 요청입니다.");
      }
      redisTemplate.opsForValue().decrement(key);
      redisTemplate.opsForList().remove("recommendList:" + feedId,1, userId);
    } else {

      boolean deleteUserResult = recommendMapper.deleteRecommendUser(
              new RecommendUserDelete(feedId, userId));

      if (!deleteUserResult) {
        throw new InvalidApproachException("비정상적인 요청입니다.");
      }

      boolean decrementRecommendResult = recommendMapper.decrementRecommend(feedId);

      if (!decrementRecommendResult) {
        throw new InvalidApproachException("비정상적인 요청입니다.");
      }
    }
  }

  /*
   * 스케줄러 수정 후 추가 예정.
   */

}
