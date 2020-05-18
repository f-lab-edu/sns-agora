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

/**
 * FeedCacheService 를 인터페이스를 구현하여 전략 패턴으로 디자인한 이유
 * 현재는 해당 프로젝트에서 레디스를 활용하여 캐시를 사용하고 있습니다.
 * 하지만 이 후에 레디스 보다 더 나은 방법의 캐시 저장소를 발견한다면 교체를 해야할 수 있습니다.
 * 만약 해당 서비스 강결합 되어 있다면, 서비스의 필요한 기능들을 하나하나 다시 정의하고 구현해야만 합니다.
 * 이를 인터페이스로 구현하면 정의되어 있는 기능을 구현하기만 하면 됩니다.
 * 즉, 이 후 기술변화에 대한 서비스 계층의 코드가 영향을 받지 않토록 결합도를 낮추기 위해서
 * 해당 서비스를 인터페이스를 통하여 추상화하였습니다.
 */
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

  @Autowired
  ObjectMapper objectMapper;

  @Override
  public FeedInfo getFeedInfoFromCache(int feedId, String userId, FriendStatus friendStatus) {

    String feedInfoKey = makeCacheKey(CacheKeyPrefix.FEED, feedId);

    String feedInfoStrCache = cacheStrRedisTemplate.boundValueOps(feedInfoKey).get();

    if (feedInfoStrCache == null) {
      return null;
    } else {

      FeedInfoCache feedInfoCache;

      try {

        feedInfoCache = objectMapper.readValue(feedInfoStrCache, FeedInfoCache.class);
      } catch (JsonProcessingException e) {
        throw new SerializationException("변환에 실패하였습니다.", e);
      }

      PublicScope publicScope = PublicScope.valueOf(feedInfoCache.getPublicScope());

      if (!friendService.isFeedReadableByFriendStatus(publicScope, friendStatus)) {
        throw new InvalidApproachException("유효하지 않은 요청입니다.");
      }

      GoodStatus goodPushedStatus = getGoodPushedCache(feedId, userId);
      boolean goodPushed = goodPushedStatus == GoodStatus.PUSHED;

      return FeedInfo.from(feedInfoCache, goodPushed);

    }
  }


  /*
  * zset에 feedId 와 가중치를 함께 저장하는 이유
  현재 score 에는 feedId가 해당 set 에 입력된 시간이 timestamp 형태로 저장되어 있습니다.
  이유는 Batch Insert 를 수행할 때, 이전에 insert 된 시간과 비교해서 이 후에 저장 되는 feedId만 조회하기 위함입니다.
  일반 List에서 조회 시에는 O(N)의 시간이 걸리는 반면, Sorted Set에서 zrange by score 로 조회하게 되면
  O(log(N)+M)의 시간이 걸리게 됩니다.
  Batch Insert의 간격을 짧게 가져간다면 Sorted Set에서 score를 통해 비교 후 일부 데이터를 가져오는 것이
  더 빠르다고 생각했기 때문에 Sorted Set 으로 관리하였습니다.
   */
  @Override
  public void addGoodPushedToCache(String userId, int feedId) {

    String goodPushedKey = makeCacheKey(CacheKeyPrefix.GOODPUSHED, userId);

    double time = Timestamp.valueOf(LocalDateTime.now()).getTime();

    zSetOps.add(goodPushedKey, feedId, time);
    cacheRedisTemplate.expire(goodPushedKey,
            cacheRedisTemplate.getExpire("userInfo:" + userId) + 60L,
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

    return "goodPushed:" + userId;
  }

  @Override
  public String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, int feedId) {

    String key;

    switch (cacheKeyPrefix) {
      case FEED:
        key = "feedInfo:" + feedId;
        break;

      case GOOD:
        key = "good:" + feedId;
        break;

      default:
        throw new InvalidApproachException("유효하지 않은 키입니다.");
    }
    return key;
  }
}
