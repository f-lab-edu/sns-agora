package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedInfo;

import java.util.List;

public interface FeedCacheService {

  void setFeedInfoCache(List<FeedInfo> feedInfoList, long expire);

  List<FeedInfo> getLatestALLFeedList();

  void setFeedListCache(List<Feed> feedList, String userId, long expire);
}
