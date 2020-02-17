package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.FeedList;
import com.ht.project.snsproject.model.feed.FeedVO;

import java.util.List;

public interface FeedService {
    int feedUpload(FeedVO feedVo, String userId);

    List<FeedList> getFeedList(String userId, Pagination pagination);

}
