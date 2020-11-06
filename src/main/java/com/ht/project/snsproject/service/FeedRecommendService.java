package com.ht.project.snsproject.service;


import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.FeedInfo;

import java.util.List;

/**
 * 인터페이스로 추상화를 한 이유
 * 현재는 최신 순의 데이터를 추천 알고리즘으로 사용하지만
 * 추후에 다른 API를 사용하거나, 다른 알고리즘을 통해서
 * 추천 알고리즘을 구현하기 위해서는 결합도를 느슨하게 하는 것이
 * 유리하도 생각하여 추상화를 하여 구현하였습니다.
 */
public interface FeedRecommendService {

  List<FeedInfo> findLatestAllFeedList(String userId, Pagination pagination);
}
