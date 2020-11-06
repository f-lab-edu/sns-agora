package com.ht.project.snsproject.service;

import com.ht.project.snsproject.mapper.FeedRecommendMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static com.ht.project.snsproject.quartz.FeedRecommendCacheService.RECOMMEND_LIST;

@Service
public class FeedRecommendServiceImpl implements FeedRecommendService {

  @Autowired
  @Qualifier("cacheStrRedisTemplate")
  private StringRedisTemplate cacheStrRedisTemplate;

  @Autowired
  private FeedRecommendMapper feedRecommendMapper;

  @Autowired
  private FeedCacheService feedCacheService;


  @Transactional(readOnly = true)
  @Override
  public List<FeedInfo> findLatestAllFeedList(String userId, Pagination pagination) {

    List<FeedInfo> feedInfoList = null;

    if(pagination.getCursor() == null && cacheStrRedisTemplate.hasKey(RECOMMEND_LIST) != null) {

      feedInfoList = feedCacheService.getLatestALLFeedList();
    }

    if(feedInfoList == null || feedInfoList.isEmpty()) {

      feedInfoList = feedRecommendMapper.findLatestAllFeedList(new FeedInfoParam(userId, pagination));
    }

    feedInfoList.sort(Comparator.comparing(FeedInfo::getId, Comparator.reverseOrder()));
    feedCacheService.setFeedInfoCache(feedInfoList, 60L);

    return feedInfoList;
  }
}
