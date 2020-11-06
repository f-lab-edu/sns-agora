package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedWriteDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeedService {

  void feedUpload(List<MultipartFile> files, FeedWriteDto feedWriteDto, String userId);

  List<Feed> findFeedListByUserId(String userId, String targetId, Pagination pagination);

  Feed findFeedByFeedId(String userId, String targetId, int feedId);

  void deleteFeed(int id, String userId);

  void updateFeed(List<MultipartFile> files,
                  FeedWriteDto feedUpdateParam, int feedId, String userId);

  List<Feed> findFriendsFeedListByUserId(String userId, Pagination pagination);

  Object findMyFeedByFeedId(int feedId, String targetId, String userId);

  Object findFriendsFeedFeedId(int feedId, String targetId, String userId);

  Object findAllFeedByFeedId(int feedId, String targetId, String userId);

  List<FeedInfo> findFeedListByFeedIdList(List<Integer> recommendIdx);
}
