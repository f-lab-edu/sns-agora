package com.ht.project.snsproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
  private FriendService friendService;

  /*
  @Resource 를 통한 빈 주입이 아닌 @Qualifier 를 통한 부가정보를 사용한 이유
  - @Resource 를 활용할 때, 빈 이름은 변경되기가 쉽고 그 자체로 의미 부여가 쉽지 않음.
  - 빈 이름과는 별도로 추가적인 메타정보를 지정해서 의미를 부여해놓고 @Autowired에서 사용할 수 있도록 하는
    @Autowired 가 훨씬 직관적이고 깔끔하다.
  - 아래와 같이 선언시, cacheRedisTemplate 이라는 한정자 값을 가진 빈으로 자동와이어링 대상을 제한할 수 있다.
   */
  @Autowired
  @Qualifier("cacheStrRedisTemplate")
  private StringRedisTemplate cacheStrRedisTemplate;

  @Resource(name = "cacheRedisTemplate")
  private ValueOperations<String, Object> valueOps;

  @Autowired
  private GoodService goodService;

  @Autowired
  private RedisCacheService redisCacheService;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public FeedInfo getFeedInfoFromCache(int feedId, String userId, FriendStatus friendStatus) {

    String feedInfoKey = redisCacheService.makeCacheKey(CacheKeyPrefix.FEED, feedId);

    String feedInfoStrCache = cacheStrRedisTemplate.boundValueOps(feedInfoKey).get();

    if (feedInfoStrCache == null) {
      return null;
    } else {

      FeedInfoCache feedInfoCache = convertJsonStrToFeedInfoCache(feedInfoStrCache);

      PublicScope publicScope = PublicScope.valueOf(feedInfoCache.getPublicScope());

      if (!friendService.isFeedReadableByFriendStatus(publicScope, friendStatus)) {
        throw new InvalidApproachException("유효하지 않은 요청입니다.");
      }

      boolean goodPushed = goodService.isGoodPushed(feedId, userId);

      return FeedInfo.from(feedInfoCache, goodPushed);

    }
  }

  @Override
  public FeedInfoCache convertJsonStrToFeedInfoCache(String jsonStr) {

    FeedInfoCache feedInfoCache;

    try {
      feedInfoCache = objectMapper.readValue(jsonStr, FeedInfoCache.class);
    } catch (JsonProcessingException e) {
      throw new SerializationException("변환에 실패하였습니다.", e);
    }

    return feedInfoCache;
  }

  @Override
  public void addFeedInfoToCache(FeedInfoCache feedInfoCache, long time, TimeUnit timeUnit) {

    String feedInfoKey = redisCacheService.makeCacheKey(CacheKeyPrefix.FEED,
            Integer.parseInt(feedInfoCache.getId()));

    valueOps.set(feedInfoKey, feedInfoCache, time, timeUnit);
  }

}
