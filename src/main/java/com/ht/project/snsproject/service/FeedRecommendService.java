package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.mapper.FeedRecommendMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.*;
import com.ht.project.snsproject.quartz.FeedRecommendCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedRecommendService {

  @Resource(name = "cacheRedisTemplate")
  private ListOperations<String, Object> listOps;

  @Autowired
  @Qualifier("cacheStrRedisTemplate")
  private StringRedisTemplate cacheStrRedisTemplate;

  @Autowired
  private FeedRecommendMapper feedRecommendMapper;

  @Autowired
  private FeedCacheService feedCacheService;

  @Autowired
  private FeedService feedService;

  @Autowired
  private GoodService goodService;

  @Transactional
  public List<RecommendFeed> getFeedRecommendListByLatestOrder(Integer cursor) {

    List<RecommendFeedInfo> recommendFeedInfoList;
    List<Integer> feedIds;

    if(cursor == null) {

      feedIds = getRecommendFeedIdsFromCache();
      recommendFeedInfoList = getRecommendFeedInfoListFromCache(feedIds);

    } else {

       recommendFeedInfoList = getRecommendFeedInfoList(cursor);

       feedIds = getRecommendFeedIds(recommendFeedInfoList);
    }

    Map<Integer, Integer> goods = goodService.getGoods(feedIds);

    return getRecommendFeedList(recommendFeedInfoList, goods);
  }

  private List<Integer> getRecommendFeedIdsFromCache() {
    return listOps.range(FeedRecommendCacheService.RECOMMEND_LIST, 0, -1).stream()
            .map(s->(Integer) s)
            .collect(Collectors.toList());
  }

  private List<Integer> getRecommendFeedIds(List<RecommendFeedInfo> recommendFeedInfoList) {

    List<Integer> feedIds = new ArrayList<>();

    for (RecommendFeedInfo recommendFeedInfo : recommendFeedInfoList) {

      feedIds.add(recommendFeedInfo.getId());
    }

    return feedIds;
  }

  private List<RecommendFeedInfo> getRecommendFeedInfoListFromCache(List<Integer> feedIds) {

    return cacheStrRedisTemplate.opsForValue()
            .multiGet(feedCacheService.makeMultiKeyList(CacheKeyPrefix.FEED,
                    feedIds)).stream()
            .map(s -> feedCacheService.convertJsonStrToFeedInfoCache(s))
            .collect(Collectors.toList()).stream()
            .map(RecommendFeedInfo::from)
            .collect(Collectors.toList());
  }

  private List<RecommendFeed> getRecommendFeedList(List<RecommendFeedInfo> recommendFeedInfoList,
                                                   Map<Integer, Integer> goods) {
    List<RecommendFeed> recommendFeedList = new ArrayList<>();

    for (RecommendFeedInfo recommendFeedInfo : recommendFeedInfoList) {

      int feedId = recommendFeedInfo.getId();

      List<FileVo> files = feedService.getFileList(recommendFeedInfo.getFileNames(),
              recommendFeedInfo.getFilePath());

      recommendFeedList.add(RecommendFeed.from(recommendFeedInfo, goods.get(feedId), files));
    }

    return recommendFeedList;
  }

  private List<RecommendFeedInfo> getRecommendFeedInfoList(Integer cursor) {

    List<RecommendFeedInfo> feedInfoList =
            feedRecommendMapper.getFeedRecommendListByLatestOrder(
            Pagination.pageInfo(cursor));

    feedCacheService.multiSetFeedInfoCache(feedInfoList.stream()
            .map(FeedInfoCache::from)
            .collect(Collectors.toList()), 60L);

    return feedInfoList;
  }

}
