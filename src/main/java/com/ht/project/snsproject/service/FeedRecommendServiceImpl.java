package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.repository.recommend.FeedRecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedRecommendServiceImpl implements FeedRecommendService {

  private final FeedRecommendRepository feedRecommendRepository;

  @Transactional(readOnly = true)
  @Override
  public List<FeedInfo> findLatestAllFeedList(Pagination pagination) {

    List<Integer> feedIdList = feedRecommendRepository.findLatestAllFeedIdList(pagination);
    List<FeedInfo> feedInfoList = feedRecommendRepository.findLatestAllFeedList(feedIdList);

    feedInfoList.sort(Comparator.comparing(FeedInfo::getId, Comparator.reverseOrder()));

    return feedInfoList;
  }
}
