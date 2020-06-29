package com.ht.project.snsproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.model.comment.CommentCount;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import com.ht.project.snsproject.model.feed.MultiSetTarget;
import com.ht.project.snsproject.model.good.Good;
import com.ht.project.snsproject.model.good.GoodPushedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisCacheService {

  private final String SPRING_CACHE_PREFIX = "spring:";

  @Autowired
  @Qualifier("cacheRedisTemplate")
  private RedisTemplate<String, Object> cacheRedisTemplate;

  @Autowired
  @Qualifier("cacheStrRedisTemplate")
  private StringRedisTemplate cacheStrRedisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  public String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, String suffix) {


    if(cacheKeyPrefix != CacheKeyPrefix.GOODPUSHED) {
      throw new InvalidApproachException("유효하지 않은 키입니다.");
    }

    return SPRING_CACHE_PREFIX + "goodPushed:" + suffix;
  }

  public String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, int feedId, String userId) {

    if(cacheKeyPrefix != CacheKeyPrefix.GOODPUSHED) {
      throw new InvalidApproachException("유효하지 않은 키입니다.");
    }

    return SPRING_CACHE_PREFIX + "goodPushed:" + feedId + ":" + userId;
  }

  public String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, int feedId) {

    String key;

    switch (cacheKeyPrefix) {
      case FEED:
        key = SPRING_CACHE_PREFIX + "feedInfo:" + feedId;
        break;

      case GOOD:
        key = SPRING_CACHE_PREFIX + "good:" + feedId;
        break;

      case COMMENTCOUNT:
        key = SPRING_CACHE_PREFIX + "commentCount:" + feedId;
        break;

      default:
        throw new InvalidApproachException("유효하지 않은 키입니다.");
    }
    return key;
  }

  public List<String> makeMultiKeyList(CacheKeyPrefix cacheKeyPrefix, List<Integer> feedIds, String userId) {

    if (cacheKeyPrefix != CacheKeyPrefix.GOODPUSHED) {
      throw new IllegalArgumentException("유효하지 않은 키입니다.");
    }

    List<String> keys = new ArrayList<>();

    for(Integer feedId : feedIds) {

      keys.add(makeCacheKey(cacheKeyPrefix, feedId, userId));
    }

    return keys;
  }

  public List<String> makeMultiKeyList(CacheKeyPrefix cacheKeyPrefix, List<Integer> feedIds) {

    List<String> keys = new ArrayList<>();

    for (Integer feedId : feedIds) {

      keys.add(makeCacheKey(cacheKeyPrefix, feedId));
    }

    return  keys;
  }

  public List<String> scanKeys(String prefix) {

    List<String> keys = new ArrayList<>();

    RedisConnection redisConnection = cacheRedisTemplate.getConnectionFactory().getConnection();
    ScanOptions options = ScanOptions.scanOptions().match(prefix).count(10).build();

    Cursor<byte[]> c = redisConnection.scan(options);
    while (c.hasNext()) {
      keys.add(new String(c.next()));
    }

    return keys;
  }

  /*
  파이프라인을 사용하는 이유

  레디스 서버는 기본적으로 TCP/IP 기반으로 데이터를 처리합니다.
  실제 동작은 클라이언트-서버 방식으로 통신하는데,
  이는 클라이언트로부터 요청을 받고 요청에 대한 처리 결과를 돌려주는 구조입니다.
  즉, 하나의 클라이언트에서 첫 번째 요청에 대한 처리가 완료되기 전에 다음 요청을 처리하지 못 합니다.
  클라이언트의 입장에서 네트워크를 통해 레디스 서버로 데이터를 전송하고 수신하는 시간은
  실제 데이터를 처리하는 시간에 포함됩니다.
  이를 '네트워크 왕복 시간(Round Trip Time)' 이라고 하는데
  한 번에 10개의 피드를 캐싱한다고 가정해보겠습니다.
  이 때, RTT 를 전송, 수신 각각 5ms 라고 가정하고, set 명령어 처리시간을 1ms 라고 하면
  반복문을 통하여 서버에 왕복할 경우 110ms의 시간이 걸리게 됩니다.
  하지만 파이프라인을 통하여 한 번의 접속으로 명령어를 수행하고 돌아오게 된다면 20ms의 시간이 걸리게 됩니다.
  그러므로 파이프라인을 사용하는 것이 성능에 더 유리하다고 판단하여 파이프라인을 사용하였습니다.

  기존 구현된 mset을 사용하지 않은 이유는 기존 API는 캐싱에 필요한 만료시간 설정이 불가하였습니다.
  그러므로 파이프라인을 통하여 커스텀한 multiSet을 구현하였습니다.

  spring-redis-data API가 제공하는 파이프라인 사용하였습니다.
  해당 document 참고하여 해당 콜백 메소드 주석 추가 예정.
   */
  private void multiSet(List<MultiSetTarget> multiSetTargetList) {

    cacheStrRedisTemplate.executePipelined(
            (RedisCallback<Object>) connection -> {

              StringRedisConnection stringRedisConn = (StringRedisConnection) connection;

              for(MultiSetTarget multiSetTarget : multiSetTargetList) {

                String key = multiSetTarget.getKey();

                stringRedisConn.set(key, multiSetTarget.getTarget());
                stringRedisConn.expire(key, multiSetTarget.getExpire());

              }
              return null;
            });
  }

  public void multiSetFeedInfoCache(List<FeedInfoCache> feedInfoCacheList, long expire) {

    List<MultiSetTarget> multiSetTargetList = new ArrayList<>();

    for (FeedInfoCache feedInfoCache : feedInfoCacheList) {

      String feedInfoToJson;

      try {
        feedInfoToJson = objectMapper.writeValueAsString(feedInfoCache);
      } catch (JsonProcessingException e) {
        throw new SerializationException("변환에 실패하였습니다.", e);
      }

      int feedId = Integer.parseInt(feedInfoCache.getId());
      String key = makeCacheKey(CacheKeyPrefix.FEED, feedId);

      multiSetTargetList.add(MultiSetTarget.builder()
              .key(key)
              .target(feedInfoToJson)
              .expire(expire)
              .build());
    }

    multiSet(multiSetTargetList);
  }

  public void multiSetGood(List<Good> goods, long expire) {

    List<MultiSetTarget> multiSetTargetList = new ArrayList<>();

    for (Good good : goods) {

      int feedId = good.getFeedId();
      String key = makeCacheKey(CacheKeyPrefix.GOOD, feedId);

      multiSetTargetList.add(MultiSetTarget.builder()
              .key(key)
              .target(String.valueOf(good.getGood()))
              .expire(expire)
              .build());
    }

    multiSet(multiSetTargetList);
  }

  public void multiSetGoodPushedStatus(List<GoodPushedStatus> goodPushedStatusList,
                                       String userId, long expire) {

    List<MultiSetTarget> multiSetTargetList = new ArrayList<>();

    for (GoodPushedStatus goodPushedStatus :  goodPushedStatusList) {

      int feedId = goodPushedStatus.getFeedId();
      String key = makeCacheKey(CacheKeyPrefix.GOODPUSHED, feedId, userId);

      multiSetTargetList.add(MultiSetTarget.builder()
              .key(key)
              .target(String.valueOf(goodPushedStatus.getPushedStatus()))
              .expire(expire)
              .build());
    }

    multiSet(multiSetTargetList);
  }

  public void multiSetCommentCount(List<CommentCount> commentCountList, long expire) {

    List<MultiSetTarget> multiSetTargetList = new ArrayList<>();

    for(CommentCount commentCount : commentCountList) {

      int feedId = commentCount.getFeedId();
      String key = SPRING_CACHE_PREFIX + "commentCounts:" + feedId;

      multiSetTargetList.add(MultiSetTarget.builder()
              .key(key)
              .target(String.valueOf(commentCount.getCommentCount()))
              .expire(expire)
              .build());
    }

    multiSet(multiSetTargetList);
  }
}
