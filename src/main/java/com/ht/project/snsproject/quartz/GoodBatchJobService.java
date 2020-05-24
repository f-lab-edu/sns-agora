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

  @Autowired
  FeedCacheService feedCacheService;

  @Autowired
  GoodBachJobMapper goodBachJobMapper;

  @Resource(name = "cacheRedisTemplate")
  ValueOperations<String, Object> valueOps;

  @Transactional
  public void executeJob() {

    log.info("The batch job has begun...");

    List<String> goodPushedKeys = feedCacheService.getCacheKeys(
            feedCacheService.makeCacheKey(CacheKeyPrefix.GOODPUSHED,"*"));

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

  public void batchInsertGoodPushedUser(List<String> goodAddKeys) {

    List<GoodUser> goodUserAddList = new ArrayList<>();

    for (String goodAddKey : goodAddKeys) {
      String[] keyArray = goodAddKey.split(":");
      int feedId = Integer.parseInt(keyArray[1]);
      String userId = keyArray[2];

      goodUserAddList.add(GoodUser.builder()
              .feedId(feedId)
              .userId(userId)
              .build());
    }

    if (goodUserAddList.size() != 0) {
      goodBachJobMapper.batchInsertGoodUserList(goodUserAddList);
    }

  }

  public void batchDeleteGoodPushedUser(List<String> goodDeleteKeys) {
    List<GoodUser> goodUserDeleteList = new ArrayList<>();

    for (String goodDeleteKey : goodDeleteKeys) {
      String[] keyArray = goodDeleteKey.split(":");
      int feedId = Integer.parseInt(keyArray[1]);
      String userId = keyArray[2];

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
