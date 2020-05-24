package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import com.ht.project.snsproject.model.good.Good;
import com.ht.project.snsproject.model.good.GoodPushedStatus;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface FeedCacheService {

  FeedInfo getFeedInfoFromCache(int feedId, String userId, FriendStatus friendStatus);

  void addFeedInfoToCache(FeedInfoCache feedInfoCache, long time, TimeUnit timeUnit);

  String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, String suffix);

  String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, int feedId, String userId);

  String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, int feedId);

  List<String> makeMultiKeyList(CacheKeyPrefix cacheKeyPrefix, List<Integer> feedIds, String userId);

  List<String> makeMultiKeyList(CacheKeyPrefix cacheKeyPrefix, List<Integer> feedIds);

  List<String> getCacheKeys(String prefix);

  void pipeliningGood(List<Good> goods, long expire);

  void pipelining(List<GoodPushedStatus> goodPushedStatuses, String userId, long expire);

  void pipeliningFeedInfoCache(List<FeedInfoCache> feedInfoCacheList, long expire);
}
