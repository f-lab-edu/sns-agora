package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.model.feed.FeedInfo;

public interface FeedCacheService {
  FeedInfo cacheToFeedInfo(int feedId, String userId, FriendStatus friendStatus);
}
