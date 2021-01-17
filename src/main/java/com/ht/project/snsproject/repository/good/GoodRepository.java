package com.ht.project.snsproject.repository.good;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.mapper.GoodMapper;
import com.ht.project.snsproject.model.feed.MultiSetTarget;
import com.ht.project.snsproject.model.good.*;
import com.ht.project.snsproject.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class GoodRepository {

  private final GoodMapper goodMapper;

  private final RedisCacheService redisCacheService;

  private final StringRedisTemplate stringRedisTemplate;

  public GoodRepository(GoodMapper goodMapper,
                        RedisCacheService redisCacheService,
                        @Qualifier("cacheStrRedisTemplate")
                                StringRedisTemplate stringRedisTemplate) {
    this.goodMapper = goodMapper;
    this.redisCacheService = redisCacheService;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Cacheable(value = "good", key = "'good:' + #feedId")
  public int getGood(int feedId) {

    return goodMapper.getGood(feedId);
  }

  @Cacheable(value = "goodPushed", key = "'goodPushed:' + #feedId + ':' + #userId")
  public boolean isGoodPushed(int feedId, String userId) {

    return goodMapper.getGoodPushedStatus(new GoodStatusParam(feedId, userId));
  }

  public Map<Integer, Integer> findGoodCountMap(List<Integer> feedIdList,
                                                List<MultiSetTarget> multiSetTargetList) {

    Map<Integer, Integer> goodCountMap = new HashMap<>();
    List<Integer> feedIdCopyList = new ArrayList<>(feedIdList);
    List<GoodCount> goodCountList = new ArrayList<>();

    findGoodCountListInCache(feedIdCopyList, goodCountList);

    if (!feedIdCopyList.isEmpty()) {
      goodCountList.addAll(findGoodCountListInDb(feedIdCopyList));
    }

    goodCountList.forEach(goodCount ->
            goodCountMap.put(goodCount.getFeedId(), goodCount.getGoodCount()));

    redisCacheService.addGoodCountInCacheList(goodCountList, multiSetTargetList);

    return goodCountMap;
  }

  public List<GoodCount> findGoodCountListInDb(List<Integer> feedIdList) {

    return goodMapper.findGoodCountList(feedIdList);
  }

  private void findGoodCountListInCache(List<Integer> feedIdList, List<GoodCount> goodCountList) {

    List<String> cacheKeys = redisCacheService.makeMultiKeyList(CacheKeyPrefix.GOOD, feedIdList);
    List<String> goodCountCacheList = stringRedisTemplate.opsForValue().multiGet(cacheKeys);

    if (goodCountCacheList != null) {
      for (int i = 0; i < goodCountCacheList.size(); i++) {

        String goodCount = goodCountCacheList.get(i);

        if (goodCount != null) {

          goodCountList.add(new GoodCount(feedIdList.get(i), Integer.parseInt(goodCount)));
          feedIdList.set(i, null);
        }
      }
    }

    feedIdList.removeIf(Objects::isNull);
  }


  public Map<Integer, Boolean> findGoodPushedStatusMap(String userId, List<Integer> feedIdList,
                                                       List<MultiSetTarget> multiSetTargetList) {

    Map<Integer, Boolean> goodPushedStatusMap = new HashMap<>();
    List<Integer> feedIdCopyList = new ArrayList<>(feedIdList);
    List<GoodPushedStatus> goodPushedStatusList = new ArrayList<>();

    findGoodPushedStatusListInCache(feedIdCopyList, userId, goodPushedStatusList);

    if (!feedIdCopyList.isEmpty()) {
      goodPushedStatusList.addAll(findGoodPushedStatusList(userId, feedIdCopyList));
    }

    goodPushedStatusList.forEach(goodPushedStatus ->
                    goodPushedStatusMap.put(goodPushedStatus.getFeedId(),
                            goodPushedStatus.getPushedStatus()));

    redisCacheService.addGoodPushedStatusInCacheList(userId,
            goodPushedStatusList, multiSetTargetList);

    return goodPushedStatusMap;
  }

  public List<GoodPushedStatus> findGoodPushedStatusList(String userId, List<Integer> feedIdList) {

    return goodMapper.findGoodPushedStatusList(new GoodPushedStatusListParam(userId, feedIdList));
  }

  private void findGoodPushedStatusListInCache(List<Integer> feedIdList, String userId,
                                               List<GoodPushedStatus> goodPushedStatusList) {

    List<String> cacheKeys = redisCacheService.makeMultiKeyList(CacheKeyPrefix.GOOD_PUSHED,
            userId, feedIdList);
    List<String> goodPushedStatusCacheList = stringRedisTemplate.opsForValue().multiGet(cacheKeys);

    if (goodPushedStatusCacheList != null) {

      for (int i = 0; i < goodPushedStatusCacheList.size(); i++) {

        String goodPushedStatus = goodPushedStatusCacheList.get(i);

        if (goodPushedStatus != null) {

          goodPushedStatusList.add(new GoodPushedStatus(feedIdList.get(i),
                  Boolean.parseBoolean(goodPushedStatus)));
          feedIdList.set(i, null);
        }
      }
    }

    feedIdList.removeIf(Objects::isNull);
  }

  public List<GoodUser> findGoodPushUserList(int feedId, Integer cursor) {

    return goodMapper.getGoodList(GoodListParam.builder()
            .feedId(feedId)
            .cursor(cursor)
            .build());
  }
}
