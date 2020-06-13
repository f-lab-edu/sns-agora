package com.ht.project.snsproject.quartz;

import com.ht.project.snsproject.mapper.FeedRecommendCacheMapper;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import com.ht.project.snsproject.service.FeedCacheService;
import com.ht.project.snsproject.service.GoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FeedRecommendCacheService {

  public static final String RECOMMEND_LIST = "recommendList";
  private static final long RECOMMEND_EXPIRE = 60L;

  @Autowired
  private FeedRecommendCacheMapper feedRecommendCacheMapper;

  @Autowired
  private FeedCacheService feedCacheService;

  @Autowired
  private GoodService goodService;

  @Autowired
  @Qualifier("cacheRedisTemplate")
  private RedisTemplate<String, Object> cacheRedisTemplate;

  public void executeJob() {

    setFeedRecommendCacheList(getFeedInfoCacheByLatestOrder());
  }

  private List<FeedInfoCache> getFeedInfoCacheByLatestOrder() {

    return feedRecommendCacheMapper.getFeedInfoByLatestOrder();
  }

  private void setFeedRecommendCacheList(List<FeedInfoCache> feedInfoCacheList) {

    List<Integer> feedIds = new ArrayList<>();

    for(FeedInfoCache feedInfoCache : feedInfoCacheList) {

      feedIds.add(Integer.valueOf(feedInfoCache.getId()));
    }

    deleteAndSetList(feedIds);
    feedCacheService.multiSetFeedInfoCache(feedInfoCacheList, RECOMMEND_EXPIRE);
    goodService.getGoods(feedIds);
  }

  private void deleteAndSetList(List<Integer> feedIds) {

    cacheRedisTemplate.execute((RedisCallback<Object>) connection -> {

      connection.multi();

      connection.del(RECOMMEND_LIST.getBytes());

      for(Integer feedId : feedIds) {
        connection.lPush(RECOMMEND_LIST.getBytes(), String.valueOf(feedId).getBytes());
      }

      connection.exec();

      return null;
    });
  }
}
