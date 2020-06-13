package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedListParam;
import com.ht.project.snsproject.model.feed.FeedUpdateParam;
import com.ht.project.snsproject.model.feed.FeedVo;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FeedService {

  void feedUpload(List<MultipartFile> files, FeedVo feedVo, String userId);

  Feed getFeed(String userId, String targetId, int id);

  List<Feed> getFeedList(String userId, String targetId, Pagination pagination);

  List<Feed> getFeeds(FeedListParam feedListParam);

  List<Feed> getFriendsFeedList(String userId, Pagination pagination);

  void deleteFeed(int id, String userId);

  void updateFeed(List<MultipartFile> files,
                  FeedUpdateParam feedUpdateParam, int feedId, String userId);
}
