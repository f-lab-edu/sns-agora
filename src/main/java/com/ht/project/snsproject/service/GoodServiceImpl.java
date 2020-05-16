package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.GoodMapper;
import com.ht.project.snsproject.model.good.GoodList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class GoodServiceImpl implements GoodService {


  private static final double DEPRECATED = 0;
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

  @Resource(name = "cacheRedisTemplate")
  ValueOperations<String, Object> valueOps;

  @Resource(name = "cacheRedisTemplate")
  ZSetOperations<String, Object> zSetOps;

  @Autowired
  GoodMapper goodMapper;

  @Autowired
  FeedCacheService feedCacheService;

  /*
  Spring cache 이용해도 괜찮을 것 같습니다.
  테스트를 위해 ttl을 5분으로 설정했습니다.
  실제 배치 성능을 위해서 캐시를 30초 이내로 짧게 설정할 예정입니다.
  -> 정확한 시간은 이유와 함께 생각해보고 설정 예정.
   */
  @Override
  public Integer getGood(int feedId) {

    String goodKey = feedCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    Integer good = (Integer) valueOps.get(goodKey);

    if(good ==null){
      good = goodMapper.getGood(feedId);
      valueOps.set(goodKey, good, 5L, TimeUnit.MINUTES);
    }

    return good;
  }


  @Override
  public GoodList getGoodList(int feedId, long cursor) {
    return null;
  }

  @Override
  public void addGood(int feedId, String userId) {

    String goodPushedKey = feedCacheService.makeCacheKey(CacheKeyPrefix.GOODPUSHED, userId);
    String goodKey = feedCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    getGood(feedId);

    Double isGoodPushed = zSetOps.score(goodPushedKey, feedId);

    if ((isGoodPushed == null) || (isGoodPushed == DEPRECATED)) {
      zSetOps.add(goodPushedKey, feedId, Timestamp.valueOf(LocalDateTime.now()).getTime());

    } else {
      throw new DuplicateRequestException("중복된 요청입니다.");
    }

    valueOps.increment(goodKey);
  }

  @Override
  public void cancelGood(int feedId, String userId) {

    String goodStatusKey = feedCacheService.makeCacheKey(CacheKeyPrefix.GOODPUSHED, userId);
    String goodKey = feedCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    getGood(feedId);

    Double isGoodPushed = zSetOps.score(goodStatusKey, feedId);

    if ((isGoodPushed == null) || (isGoodPushed == DEPRECATED)) {
      throw new InvalidApproachException("비정상적인 요청입니다.");
    } else {
      zSetOps.add(goodStatusKey, feedId, DEPRECATED);
    }

    valueOps.decrement(goodKey);
  }
}
