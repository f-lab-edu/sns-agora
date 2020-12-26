package com.ht.project.snsproject.repository.recommend;

import com.ht.project.snsproject.mapper.FeedRecommendMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.repository.feed.FeedRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ht.project.snsproject.quartz.FeedRecommendCacheService.RECOMMEND_LIST;

@Repository
public class FeedRecommendRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  private final FeedRepository feedRepository;

  private final FeedRecommendMapper feedRecommendMapper;

  public FeedRecommendRepository(@Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                                 FeedRepository feedRepository,
                                 FeedRecommendMapper feedRecommendMapper) {
    this.redisTemplate = redisTemplate;
    this.feedRepository = feedRepository;
    this.feedRecommendMapper = feedRecommendMapper;
  }

  public List<Integer> findLatestAllFeedIdList(Pagination pagination) {

    List<Integer> recommendIdx = new ArrayList<>();

    if(pagination.getCursor() == null && redisTemplate.hasKey(RECOMMEND_LIST) != null) {

      recommendIdx = Objects.requireNonNull(
                      redisTemplate.opsForList().range(RECOMMEND_LIST, 0, -1),
                      "추천 목록이 존재하지 않습니다.")
                      .stream()
                      .map(s->(Integer) s)
                      .collect(Collectors.toList());
    }

    if(recommendIdx.isEmpty()) {

      recommendIdx = feedRecommendMapper.findFeedIdByLatestOrder(pagination);
    }


    return recommendIdx;
  }

  public List<FeedInfo> findLatestAllFeedList(List<Integer> feedIdList) {

    List<FeedInfo> feedInfoList = new ArrayList<>();
    List<Integer> feedIdCopyList = new ArrayList<>(feedIdList);
    feedRepository.findFeedInfoListInCache(feedInfoList, feedIdCopyList);

    if(!feedIdList.isEmpty()) {

      feedInfoList.addAll(feedRepository.findFeedListByFeedIdList(feedIdCopyList));
    }

    return feedInfoList;
  }
}
