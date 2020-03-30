package com.ht.project.snsproject.service;

import java.util.List;

public interface RecommendService {

    int getRecommend(int feedId);

    void initRecommendList(int feedId);

    List<String> getRecommendList(int feedId);
}
