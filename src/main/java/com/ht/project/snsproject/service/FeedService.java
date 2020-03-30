package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeedService {
    void feedUpload(List<MultipartFile> files, FeedVO feedVo, String userId);

    Feed getFeed(String userId, String targetId, int id);

    List<Feed> getFeedList(String userId, String targetId, Pagination pagination);

    List<Feed> getFeeds(FeedListParam feedListParam);

    List<Feed> getFriendsFeedList(String userId, Pagination pagination);

    void deleteFeed(int id, String userId);

    void updateFeed(List<MultipartFile> files, FeedUpdateParam feedUpdateParam, int feedId, String userId);

}
