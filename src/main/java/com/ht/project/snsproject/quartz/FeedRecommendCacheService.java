package com.ht.project.snsproject.quartz;

import com.ht.project.snsproject.mapper.FeedRecommendCacheMapper;
import com.ht.project.snsproject.service.FeedCacheService;
import com.ht.project.snsproject.service.GoodService;
import com.ht.project.snsproject.service.RedisCacheService;
import io.lettuce.core.RedisCommandExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

  @Resource(name = "cacheRedisTemplate")
  private ListOperations<String, Object> listOps;

  public void executeJob() {

    Boolean existKey = cacheRedisTemplate.hasKey(RECOMMEND_LIST);

    if(existKey != null && !existKey) {

      setFeedRecommendCacheList(getFeedInfoCacheByLatestOrder());
    }
  }

  private List<Integer> getFeedInfoCacheByLatestOrder() {

    return feedRecommendCacheMapper.getFeedInfoByLatestOrder();
  }

  private void setFeedRecommendCacheList(List<Integer> feedIds) {

      updateList(feedIds);
  }

  /*
  multi(); 호출과 함께 트랜잭션이 시작되고,
  작업이 완료가 되면 트랜잭션 commit 까지 완료시킵니다.
  하지만 작업 중간에 Exception이 발생한다면 트랜잭션 전체를 취소하고,
  Unchecked Exception을 던지도록 합니다.
   */
  private void updateList(List<Integer> feedIds) {

    cacheRedisTemplate.execute((RedisCallback<Object>) connection -> {

      connection.multi();//트랜잭션 시작

      try {

        for (Integer feedId : feedIds) {
          connection.lPush(RECOMMEND_LIST.getBytes(), String.valueOf(feedId).getBytes());
        }
        connection.expire(RECOMMEND_LIST.getBytes(), RECOMMEND_EXPIRE);
        connection.exec();//트랙잭션 커밋

      } catch (Exception e) {

        connection.discard();//트랜잭션 취소
        throw new RedisCommandExecutionException("업데이트 오류");
      }

      return null;
    });
  }
}
