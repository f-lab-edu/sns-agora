package com.ht.project.snsproject.service;

import java.util.List;

public interface RecommendService {

    int getRecommend(int feedId);

    List<String> getRecommendList(int feedId);

    void increaseRecommend(int feedId, String userId);

    void cancelRecommend(int feedId, String userId);
}
