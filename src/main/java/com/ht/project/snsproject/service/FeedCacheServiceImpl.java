package com.ht.project.snsproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import com.ht.project.snsproject.model.good.Good;
import com.ht.project.snsproject.model.good.GoodPushedStatus;
import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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

  @Autowired
  FriendService friendService;

  /*
  @Resource 를 통한 빈 주입이 아닌 @Qualifier 를 통한 부가정보를 사용한 이유
  - @Resource 를 활용할 때, 빈 이름은 변경되기가 쉽고 그 자체로 의미 부여가 쉽지 않음.
  - 빈 이름과는 별도로 추가적인 메타정보를 지정해서 의미를 부여해놓고 @Autowired에서 사용할 수 있도록 하는
    @Autowired 가 훨씬 직관적이고 깔끔하다.
  - 아래와 같이 선언시, cacheRedisTemplate 이라는 한정자 값을 가진 빈으로 자동와이어링 대상을 제한할 수 있다.
   */
  @Autowired
  @Qualifier("cacheRedisTemplate")
  RedisTemplate<String, Object> cacheRedisTemplate;

  @Autowired
  @Qualifier("cacheStrRedisTemplate")
  StringRedisTemplate cacheStrRedisTemplate;

  @Resource(name = "cacheRedisTemplate")
  ValueOperations<String, Object> valueOps;

  @Autowired
  GoodService goodService;

  @Autowired
  RedisClient redisClient;

  @Autowired
  public ObjectMapper objectMapper;

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

      boolean goodPushed = goodService.isGoodPushed(feedId, userId);

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
  public void addFeedInfoToCache(FeedInfoCache feedInfoCache, long time, TimeUnit timeUnit) {

    String feedInfoKey = makeCacheKey(CacheKeyPrefix.FEED, Integer.parseInt(feedInfoCache.getId()));

    valueOps.set(feedInfoKey, feedInfoCache, time, timeUnit);
  }


  @Override
  public String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, String suffix) {

    if(cacheKeyPrefix != CacheKeyPrefix.GOODPUSHED) {
      throw new InvalidApproachException("유효하지 않은 키입니다.");
    }

    return "goodPushed:" + suffix;
  }


  public String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, int feedId, String userId) {

    if(cacheKeyPrefix != CacheKeyPrefix.GOODPUSHED) {
      throw new InvalidApproachException("유효하지 않은 키입니다.");
    }

    return "goodPushed:" + feedId + ":" + userId;
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

  @Override
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

  @Override
  public List<String> makeMultiKeyList(CacheKeyPrefix cacheKeyPrefix, List<Integer> feedIds) {

    List<String> keys = new ArrayList<>();

    for (Integer feedId : feedIds) {

      keys.add(makeCacheKey(cacheKeyPrefix, feedId));
    }

    return  keys;
  }

  @Override
  public List<String> getCacheKeys(String prefix) {

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
  lettuce document 참조.
  각 객체 설명 추가 필요.
   */
  @Override
  public void pipeliningGood(List<Good> goods, long expire) {

    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisAsyncCommands<String, String> commands = connection.async();

    // disable auto-flushing
    commands.setAutoFlushCommands(false);

    // perform a series of independent calls
    List<RedisFuture<?>> futures = new ArrayList<>();

    for (Good good : goods) {

      int feedId = good.getFeedId();
      String key = makeCacheKey(CacheKeyPrefix.GOOD, feedId);
      futures.add(commands.set(key, String.valueOf(good.getGood())));
      futures.add(commands.expire(key, expire));
    }

    // write all commands to the transport layer
    commands.flushCommands();

    // synchronization example: Wait until all futures complete
    boolean result = LettuceFutures.awaitAll(5, TimeUnit.SECONDS,
            futures.toArray(new RedisFuture[futures.size()]));

    // later
    connection.close();
  }

  @Override
  public void pipelining(List<GoodPushedStatus> goodPushedStatuses, String userId, long expire) {

    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisAsyncCommands<String, String> commands = connection.async();

    // disable auto-flushing
    commands.setAutoFlushCommands(false);

    // perform a series of independent calls
    List<RedisFuture<?>> futures = new ArrayList<>();
    for (GoodPushedStatus goodPushedStatus :  goodPushedStatuses) {

      int feedId = goodPushedStatus.getFeedId();
      String key = makeCacheKey(CacheKeyPrefix.GOODPUSHED, feedId, userId);
      futures.add(commands.set(key, String.valueOf(goodPushedStatus.isPushedStatus())));
      futures.add(commands.expire(key, expire));
    }

    // write all commands to the transport layer
    commands.flushCommands();

    // synchronization example: Wait until all futures complete
    boolean result = LettuceFutures.awaitAll(5, TimeUnit.SECONDS,
            futures.toArray(new RedisFuture[futures.size()]));

    // later
    connection.close();
  }

  @Override
  public void pipeliningFeedInfoCache(List<FeedInfoCache> feedInfoCacheList, long expire) {

    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisAsyncCommands<String, String> commands = connection.async();

    // disable auto-flushing
    commands.setAutoFlushCommands(false);

    // perform a series of independent calls
    List<RedisFuture<?>> futures = new ArrayList<>();
    for (FeedInfoCache feedInfoCache :  feedInfoCacheList) {

      String feedInfoToJson;

      try {
        feedInfoToJson = objectMapper.writeValueAsString(feedInfoCache);
      } catch (JsonProcessingException e) {
        throw new SerializationException("변환에 실패하였습니다.", e);
      }

      int feedId = Integer.parseInt(feedInfoCache.getId());
      String key = makeCacheKey(CacheKeyPrefix.FEED, feedId);
      futures.add(commands.set(key, feedInfoToJson));
      futures.add(commands.expire(key, expire));
    }

    // write all commands to the transport layer
    commands.flushCommands();

    // synchronization example: Wait until all futures complete
    boolean result = LettuceFutures.awaitAll(5, TimeUnit.SECONDS,
            futures.toArray(new RedisFuture[futures.size()]));

    // later
    connection.close();
  }
}
