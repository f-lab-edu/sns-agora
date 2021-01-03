package com.ht.project.snsproject.repository.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.*;
import com.ht.project.snsproject.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class FeedRepository {

  private final FeedMapper feedMapper;

  private final RedisCacheService redisCacheService;

  private final RedisTemplate<String, Object> cacheRedisTemplate;

  private final ObjectMapper objectMapper;

  public FeedRepository(FeedMapper feedMapper,
                        RedisCacheService redisCacheService,
                        @Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> cacheRedisTemplate,
                        @Qualifier("cacheObjectMapper") ObjectMapper objectMapper) {
    this.feedMapper = feedMapper;
    this.redisCacheService = redisCacheService;
    this.cacheRedisTemplate = cacheRedisTemplate;
    this.objectMapper = objectMapper;
  }

  public void insertFeed(FeedInsert feedInsert) {

    feedMapper.feedUpload(feedInsert);
  }

  @Cacheable(value = "feedInfo", key = "'feedInfo:' + #feedId")
  public Object findMyFeedByFeedId(int feedId) {

    FeedInfo feed = feedMapper.findMyFeedByFeedId(feedId);

    if(feed.getId() == null) {

      throw new IllegalArgumentException("일치하는 데이터가 존재하지 않습니다.");
    }

    return feed;
  }

  @Cacheable(value = "feedInfo", key = "'feedInfo:' + #feedId")
  public Object findFriendsFeedByFeedId(int feedId) {

    FeedInfo feed = feedMapper.findFriendsFeedByFeedId(feedId);

    if(feed.getId() == null) {

      throw new IllegalArgumentException("일치하는 데이터가 존재하지 않습니다.");
    }

    return feed;
  }

  @Cacheable(value = "feedInfo", key = "'feedInfo:' + #feedId")
  public Object findAllFeedByFeedId(int feedId) {

    FeedInfo feed = feedMapper.findAllFeedByFeedId(feedId);

    if(feed.getId() == null) {

      throw new IllegalArgumentException("일치하는 데이터가 존재하지 않습니다.");
    }

    return feed;
  }

  @CacheEvict(value = "feedInfo", key = "'feedInfo:' + #feedId")
  public boolean deleteFeed(int feedId, String userId) {

    return feedMapper.deleteFeed(new FeedDeleteParam(feedId, userId));
  }

  @CacheEvict(value = "feedInfo", key = "'feedInfo:' + #feedId")
  public boolean updateFeed(int feedId, String userId, FeedWriteDto feedWriteDto) {

    return feedMapper.updateFeed(FeedUpdate.create(feedId, userId, feedWriteDto, LocalDateTime.now()));
  }

  public List<Integer> findMyFeedIdListByUserId(String targetId, Pagination pagination) {

    return feedMapper.findMyFeedIdListByUserId(new FeedIdListParam(targetId, pagination));
  }

  public List<Integer> findFriendFeedIdListByUserId(String targetId, Pagination pagination) {

    return feedMapper.findFriendFeedIdListByUserId(new FeedIdListParam(targetId, pagination));
  }

  public List<Integer> findALLFeedIdListByUserId(String targetId, Pagination pagination) {

    return feedMapper.findAllFeedIdListByUserId(new FeedIdListParam(targetId, pagination));
  }

  public List<FeedInfo> findFeedInfoList(List<Integer> feedIdList, List<MultiSetTarget> multiSetTargetList) {

    List<Integer> feedIdCopyList = new ArrayList<>(feedIdList);
    List<FeedInfo> feedInfoList = new ArrayList<>();
    findFeedInfoListInCache(feedInfoList,feedIdCopyList);

    if (!feedIdCopyList.isEmpty()) {
      feedInfoList.addAll(findFeedInfoListInDb(feedIdCopyList));
    }
    redisCacheService.addFeedInfoInCacheList(feedInfoList, multiSetTargetList);

    return feedInfoList;
  }

  public List<FeedInfo> findFeedInfoListInDb(List<Integer> feedIdList) {

    return feedMapper.findFeedInfoListByFeedIdList(feedIdList);
  }

  public void findFeedInfoListInCache(List<FeedInfo> feedInfoList, List<Integer> feedIdList) {

    List<String> cacheKeys = redisCacheService.makeMultiKeyList(CacheKeyPrefix.FEED, feedIdList);
    List<Object> feedInfoCacheList = cacheRedisTemplate.opsForValue().multiGet(cacheKeys);

    if(feedInfoCacheList != null) {

      for (int i=0; i<feedInfoCacheList.size(); i++) {

        Object feedInfoCache = feedInfoCacheList.get(i);

        if (feedInfoCache != null) {
          FeedInfo feedInfo = objectMapper.convertValue(feedInfoCache, FeedInfo.class);
          feedInfoList.add(feedInfo);
          feedIdList.set(i, null);
        }
      }
    }

    feedIdList.removeIf(Objects::isNull);
  }

  public List<FeedInfo> findFeedListByFeedIdList(List<Integer> recommendIdx) {
    return feedMapper.findFeedInfoListByFeedIdList(recommendIdx);
  }

  public List<Integer> findFriendsFeedIdList(String userId, Pagination pagination) {

    return feedMapper.findFriendsFeedIdList(new FriendsFeedIdParam(userId, pagination));
  }
}
