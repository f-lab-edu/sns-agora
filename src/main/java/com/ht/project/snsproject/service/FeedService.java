package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedListParam;
import com.ht.project.snsproject.model.feed.FeedUpdateParam;
import com.ht.project.snsproject.model.feed.FeedVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeedService {

  void feedUpload(List<MultipartFile> files, FeedVo feedVo, String userId);

  Feed getFeed(String userId, String targetId, int id);

  List<Feed> getFeedListByUser(String userId, String targetId, Pagination pagination);

  List<Feed> getFeedsByUser(FeedListParam feedListParam);

  List<Feed> getFriendsFeedList(String userId, Pagination pagination);

  void deleteFeed(int id, String userId);

  void updateFeed(List<MultipartFile> files,
                  FeedUpdateParam feedUpdateParam, int feedId, String userId);
}
