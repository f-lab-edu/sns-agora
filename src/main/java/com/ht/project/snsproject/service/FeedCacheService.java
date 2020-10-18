package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoCache;

import java.util.concurrent.TimeUnit;

public interface FeedCacheService {

  FeedInfo getFeedInfoFromCache(int feedId, String userId, FriendStatus friendStatus);

  FeedInfoCache convertJsonStrToFeedInfoCache(String jsonStr);

  void addFeedInfoToCache(FeedInfoCache feedInfoCache, long time, TimeUnit timeUnit);

}
