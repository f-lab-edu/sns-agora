package com.ht.project.snsproject.quartz;

import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.mapper.GoodBachJobMapper;
import com.ht.project.snsproject.model.good.GoodUser;
import com.ht.project.snsproject.service.FeedCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class GoodBatchJobService {

  private AtomicInteger count = new AtomicInteger();
  /*
   배열의 인덱스값은 상수로 정의해놓고 사용하는게 유지보수성에 유리.
   */
  private final int FEED_ID = 2;
  private final int USER_ID = 3;


  @Autowired
  private FeedCacheService feedCacheService;

  @Autowired
  private GoodBachJobMapper goodBachJobMapper;

  @Resource(name = "cacheRedisTemplate")
  private ValueOperations<String, Object> valueOps;

  @Transactional
  public void executeJob() {

    log.info("The batch job has begun...");

    List<String> goodPushedKeys = feedCacheService.scanKeys(
            feedCacheService.makeCacheKey(CacheKeyPrefix.GOODPUSHED, "*"));

    List<Object> values = valueOps.multiGet(goodPushedKeys);
    List<String> goodAddKeys = new ArrayList<>();
    List<String> goodDeleteKeys = new ArrayList<>();

    for (int i=0; i<values.size(); i++) {

      Boolean goodPushed = (Boolean) values.get(i);

      if(goodPushed != null) {
        if(goodPushed) {

          goodAddKeys.add(goodPushedKeys.get(i));
        } else {

          goodDeleteKeys.add(goodPushedKeys.get(i));
        }
      }
    }

    batchInsertGoodPushedUser(goodAddKeys);
    batchDeleteGoodPushedUser(goodDeleteKeys);

    count.incrementAndGet();
    log.info("Batch job has finished...");

  }

  /*
  batch Insert는
  내부에서만 사용하므로 접근제한자를 private으로 선언하여
  혹시 모를 다른 클래스에서의 호출을 미연에 방지 필요!
   */
  private void batchInsertGoodPushedUser(List<String> goodAddKeys) {

    List<GoodUser> goodUserAddList = new ArrayList<>();

    for (String goodAddKey : goodAddKeys) {
      String[] keyArray = goodAddKey.split(":");
      int feedId = Integer.parseInt(keyArray[FEED_ID]);
      String userId = keyArray[USER_ID];

      goodUserAddList.add(GoodUser.builder()
              .feedId(feedId)
              .userId(userId)
              .build());
    }

    if (goodUserAddList.size() != 0) {
      goodBachJobMapper.batchInsertGoodUserList(goodUserAddList);
    }

  }

  private void batchDeleteGoodPushedUser(List<String> goodDeleteKeys) {
    List<GoodUser> goodUserDeleteList = new ArrayList<>();

    for (String goodDeleteKey : goodDeleteKeys) {
      String[] keyArray = goodDeleteKey.split(":");
      int feedId = Integer.parseInt(keyArray[FEED_ID]);
      String userId = keyArray[USER_ID];

      goodUserDeleteList.add(GoodUser.builder()
              .feedId(feedId)
              .userId(userId)
              .build());
    }

    if (goodUserDeleteList.size() != 0) {
      goodBachJobMapper.batchDeleteGoodUserList(goodUserDeleteList);
    }
  }

  public int getNumberOfInvocations() {
    return count.get();
  }


}
