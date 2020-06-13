package com.ht.project.snsproject.quartz;

import com.ht.project.snsproject.mapper.FeedRecommendCacheMapper;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import com.ht.project.snsproject.service.FeedCacheService;
import com.ht.project.snsproject.service.GoodService;
import com.ht.project.snsproject.service.RedisCacheService;
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
  private RedisCacheService redisCacheService;

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
    redisCacheService.multiSetFeedInfoCache(feedInfoCacheList, RECOMMEND_EXPIRE);
    goodService.getGoods(feedIds);
  }

  private void deleteAndSetList(List<Integer> feedIds) {

    cacheRedisTemplate.execute((RedisCallback<Object>) connection -> {

      connection.multi();//트랜잭션 시작

      try {
        connection.del(RECOMMEND_LIST.getBytes());

        for (Integer feedId : feedIds) {
          connection.lPush(RECOMMEND_LIST.getBytes(), String.valueOf(feedId).getBytes());
        }

      } catch (Exception e) {

        connection.discard();//트랜잭션 취소
      } finally {

        connection.exec();//트랙잭션 커밋
      }

      return null;
    });
  }
}
