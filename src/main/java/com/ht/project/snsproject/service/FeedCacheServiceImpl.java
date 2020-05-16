package com.ht.project.snsproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.GoodStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class FeedCacheServiceImpl implements FeedCacheService{

  private static final long DEPRECATED = 0;

  @Autowired
  FriendService friendService;

  @Autowired
  @Qualifier("cacheRedisTemplate")
  RedisTemplate<String, Object> cacheRedisTemplate;

  @Autowired
  @Qualifier("cacheStrRedisTemplate")
  StringRedisTemplate cacheStrRedisTemplate;

  @Resource(name = "cacheRedisTemplate")
  ZSetOperations<String, Object> zSetOps;

  @Resource(name = "cacheRedisTemplate")
  ValueOperations<String, Object> valueOps;

  /**
   * ObjectMapper 클래스
   * 주석 추가 예정.
   */
  ObjectMapper mapper = new ObjectMapper();

  @Override
  public FeedInfo getFeedInfoFromCache(int feedId, String userId, FriendStatus friendStatus) {

    String feedInfoKey = makeCacheKey(CacheKeyPrefix.FEED, feedId);

    String feedInfoStrCache = cacheStrRedisTemplate.boundValueOps(feedInfoKey).get();

    if (feedInfoStrCache == null) {
      return null;
    } else {

      FeedInfoCache feedInfoCache;

      try {

        feedInfoCache = mapper.readValue(feedInfoStrCache, FeedInfoCache.class);
      } catch (JsonProcessingException e) {
        throw new SerializationException("변환에 실패하였습니다.", e);
      }

      PublicScope publicScope = PublicScope.valueOf(feedInfoCache.getPublicScope());

      if (!friendService.isFeedReadableByFriendStatus(publicScope, friendStatus)) {
        throw new InvalidApproachException("유효하지 않은 요청입니다.");
      }

      GoodStatus goodPushedStatus = getGoodPushedCache(feedId, userId);
      boolean goodPushed = goodPushedStatus == GoodStatus.PUSHED;

      return FeedInfo.cacheToFeedInfo(feedInfoCache, goodPushed);

    }
  }

  @Override
  public void addGoodPushedToCache(String userId, int feedId) {

    String goodPushedKey = makeCacheKey(CacheKeyPrefix.GOODPUSHED, userId);

    double time = Timestamp.valueOf(LocalDateTime.now()).getTime();

    zSetOps.add(goodPushedKey, feedId, time);
    cacheRedisTemplate.expire(goodPushedKey,
            cacheRedisTemplate.getExpire("userInfo:"+userId) + 60L,
            TimeUnit.SECONDS);//세션 만료 시간 + 60초로 설정 로그아웃 이후에도 한 번 더 batch insert를 해야하기 때문.
  }

  @Override
  public void addFeedInfoToCache(FeedInfoCache feedInfoCache, long time, TimeUnit timeUnit) {

    String feedInfoKey = makeCacheKey(CacheKeyPrefix.FEED, Integer.parseInt(feedInfoCache.getId()));

    valueOps.set(feedInfoKey, feedInfoCache, time, timeUnit);
  }

  @Override
  public GoodStatus getGoodPushedCache(int feedId, String userId) {

    String goodPushedKey = makeCacheKey(CacheKeyPrefix.GOODPUSHED, userId);

    Double isGoodPushed = zSetOps.score(goodPushedKey, feedId);

    GoodStatus goodPushed;

    if (isGoodPushed == null) {

      goodPushed = GoodStatus.NOT_PUSHED;
    } else if (isGoodPushed == DEPRECATED) {

      goodPushed = GoodStatus.DEPRECATED;
    } else {

      goodPushed = GoodStatus.PUSHED;
    }

    return goodPushed;
  }

  @Override
  public String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, String userId) {

    if(cacheKeyPrefix != CacheKeyPrefix.GOODPUSHED) {
      throw new InvalidApproachException("유효하지 않은 키입니다.");
    }

    return "goodPushed:"+userId;
  }

  @Override
  public String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, int feedId) {

    String key;

    switch (cacheKeyPrefix) {
      case FEED:
        key = "feedInfo:"+feedId;
        break;

      case GOOD:
        key = "good:"+feedId;
        break;

      default:
        throw new InvalidApproachException("유효하지 않은 키입니다.");
    }
    return key;
  }
}
