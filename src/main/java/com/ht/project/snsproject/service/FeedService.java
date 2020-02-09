package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.feed.FeedVO;

public interface FeedService {
    void feedUpload(FeedVO feedVo, String userId, String path);
}
