package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.GoodMapper;
import com.ht.project.snsproject.model.good.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class GoodServiceImpl implements GoodService {


  @Resource(name = "cacheRedisTemplate")
  private ValueOperations<String, Object> valueOps;

  @Autowired
  private GoodMapper goodMapper;

  @Autowired
  private RedisCacheService redisCacheService;


  /*
  Spring cache 이용해도 괜찮을 것 같습니다.
  테스트를 위해 ttl을 5분으로 설정했습니다.
  실제 배치 성능을 위해서 캐시를 30초 이내로 짧게 설정할 예정입니다.
  -> 정확한 시간은 이유와 함께 생각해보고 설정 예정.
   */
  @Cacheable(value = "good", key = "'good:' + #feedId")
  @Override
  public Integer getGood(int feedId) {

    return goodMapper.getGood(feedId);
  }

  @Transactional(readOnly = true)
  @Override
  public Map<Integer, Integer> getGoods(List<Integer> feedIds) {

    List<String> goodKeys = redisCacheService.makeMultiKeyList(CacheKeyPrefix.GOOD, feedIds);

    List<Object> values = valueOps.multiGet(goodKeys);
    List<Integer> feedIdNotInCache = new ArrayList<>();

    Map<Integer, Integer> goodsMap = new Hashtable<>();

    for (int i=0; i<values.size(); i++) {

      Integer good = (Integer) values.get(i);
      int feedId = feedIds.get(i);

      if(good != null) {
        goodsMap.put(feedId, good);

      } else {
        feedIdNotInCache.add(feedId);
      }
    }

    if (!feedIdNotInCache.isEmpty()) {

      List<Good> goods = goodMapper.getGoods(feedIdNotInCache);

      for (Good good : goods) {
        int feedId = good.getFeedId();
        feedIdNotInCache.remove((Object) feedId);
        goodsMap.put(feedId, good.getGood());
      }

      for (int feedId : feedIdNotInCache) {
        goodsMap.put(feedId, 0);
        goods.add(Good.builder()
                .feedId(feedId)
                .good(0)
                .build());
      }

      redisCacheService.multiSetGood(goods, 60L);
    }


  return goodsMap;
  }


  @Cacheable(value = "goodPushed", key = "'goodPushed:' + #feedId + ':' + #userId")
  @Override
  public boolean isGoodPushed(int feedId, String userId) {

    return goodMapper.getGoodPushedStatus(new GoodStatusParam(feedId, userId));
  }

  /*
    list를 사용해보려했으나 key value 형태에서는 hashmap이 단순하게 접근하기 쉽고, 부가적으로
    작업할 필요가 줄어들어 Map을 사용하였습니다.
   */
  @Override
  public Map<Integer, Boolean> getGoodPushedStatusesFromCache(List<Integer> feedIds, String userId) {

    List<String> goodPushedKeys = redisCacheService.makeMultiKeyList(CacheKeyPrefix.GOOD_PUSHED, feedIds, userId);
    List<Object> goodPushedStatusesFromCache = valueOps.multiGet(goodPushedKeys);

    Map<Integer, Boolean> goodPushedMap = new HashMap<>();

    for (int i=0; i<goodPushedStatusesFromCache.size(); i++) {

      Boolean value = (Boolean) goodPushedStatusesFromCache.get(i);

      if (value != null) {

        goodPushedMap.put(feedIds.get(i), value);
      }
    }

    return goodPushedMap;
  }

  @Override
  public List<GoodUser> getGoodList(int feedId, Integer cursor) {

    return goodMapper.getGoodList(GoodListParam.builder()
            .feedId(feedId)
            .cursor(cursor)
            .build());
  }

  @Override
  public void addGood(int feedId, String userId) {

    String goodPushedKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD_PUSHED, feedId, userId);
    String goodKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    getGood(feedId);

    Boolean goodPushed = (Boolean) valueOps.get(goodPushedKey);

    if ((goodPushed == null) || !goodPushed) {
      valueOps.set(goodPushedKey, true, 60L, TimeUnit.SECONDS);

    } else {
      throw new DuplicateRequestException("중복된 요청입니다.");
    }

    valueOps.increment(goodKey);
  }

  @Override
  public void cancelGood(int feedId, String userId) {

    String goodPushedKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD_PUSHED, feedId, userId);
    String goodKey = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD, feedId);

    getGood(feedId);

    Boolean goodPushed = (Boolean) valueOps.get(goodPushedKey);

    if ((goodPushed == null) || !goodPushed) {
      throw new InvalidApproachException("비정상적인 요청입니다.");
    } else {
      valueOps.set(goodPushedKey, false, 60L, TimeUnit.SECONDS);
    }

    valueOps.decrement(goodKey);
  }
}
