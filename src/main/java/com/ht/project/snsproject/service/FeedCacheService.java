package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.GoodStatus;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoCache;

import java.util.concurrent.TimeUnit;

public interface FeedCacheService {

  FeedInfo getFeedInfoFromCache(int feedId, String userId, FriendStatus friendStatus);

  void addGoodPushedToCache(String userId, int feedId);

  void addFeedInfoToCache(FeedInfoCache feedInfoCache, long time, TimeUnit timeUnit);

  GoodStatus getGoodPushedCache(int feedId, String userId);

  String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, String userId);

  String makeCacheKey(CacheKeyPrefix cacheKeyPrefix, int feedId);

}
