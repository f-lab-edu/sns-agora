package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedWriteDto;

import java.util.List;

public interface FeedCacheService {

  void setFeedInfoCache(List<FeedInfo> feedInfoList, long expire);

  List<FeedInfo> getLatestALLFeedList();

  void setFeedListCache(List<Feed> feedList, String userId, long expire);

  Object findMyFeedByFeedId(int feedId, String targetId, String userId);

  Object findFriendsFeedByFeedId(int feedId, String targetId, String userId);

  Object findAllFeedByFeedId(int feedId, String targetId, String userId);

  boolean deleteFeed(int feedId, String userId);

  boolean updateFeed(int feedId, String userId, FeedWriteDto feedWriteDto);
}
