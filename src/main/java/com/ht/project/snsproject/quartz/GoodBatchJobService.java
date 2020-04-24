package com.ht.project.snsproject.quartz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.mapper.GoodBachJobMapper;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import com.ht.project.snsproject.model.good.FeedCacheUpdateParam;
import com.ht.project.snsproject.model.good.GoodUsersParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class GoodBatchJobService {

  @Autowired
  RedisTemplate<String, Object> redisTemplate;

  @Autowired
  RedisTemplate<String, String> strValueRedisTemplate;

  @Autowired
  GoodBachJobMapper goodBachJobMapper;

  @Autowired
  StringRedisTemplate strRedisTemplate;

  private AtomicInteger count = new AtomicInteger();

  @Transactional
  public void executeJob() {

    log.info("The batch job has begun...");

    ObjectMapper objectMapper = new ObjectMapper();

    List<String> goodKeys = getKeys("good:*");
    List<String> feedInfoKeys = getKeys("feedInfo:*");

    List<String> goodStrValues = getValues(goodKeys);
    List<String> feedInfoStrValues = getValues(feedInfoKeys);


    List<GoodUsersParam> goodUsersParams = new ArrayList<>();
    List<FeedCacheUpdateParam> goods = new ArrayList<>();

    for (int i=0; i<goodKeys.size(); i++) {

      String[] keyName = goodKeys.get(i).split(":");
      int feedId = Integer.parseInt(keyName[1]);

      try {
        Integer good = objectMapper.readValue(goodStrValues.get(i), Integer.class);
        FeedInfoCache feedInfoCache = objectMapper.readValue(feedInfoStrValues.get(i), FeedInfoCache.class);
        List<String> goodList = strValueRedisTemplate.boundListOps("goodList:"+feedId).range(0,-1);

        goods.add(FeedCacheUpdateParam.create(feedInfoCache, good));

        for(String userId : goodList) {
          goodUsersParams.add(GoodUsersParam.builder()
                  .feedId(feedId)
                  .userId(userId.replaceAll("\"","")).build());
        }

      } catch (JsonProcessingException e) {
        throw new SerializationException("변환에 실패하였습니다.", e);
      }

      goodBachJobMapper.batchInsertGoodUserList(goodUsersParams);
      goodBachJobMapper.batchUpdateFeedInfo(goods);
    }
    count.incrementAndGet();
    log.info("Batch job has finished...");

  }

  public List<String> getKeys(String keysPrefix) {

    List<String> keys = new ArrayList<>();

    RedisConnection redisConnection = null;

    try {
      redisConnection = redisTemplate.getConnectionFactory().getConnection();
      ScanOptions scanOptions = ScanOptions
              .scanOptions()
              .match(keysPrefix)
              .count(10).build();

      Cursor<byte[]> c = redisConnection.scan(scanOptions);
      while (c.hasNext()) {
        keys.add(new String(c.next()));
      }
    } finally {
      redisConnection.close();
    }

    return keys;
  }

  public List<String> getValues(List<String> keys) {

    return strRedisTemplate.opsForValue().multiGet(keys);
  }

  public int getNumberOfInvocations() {
    return count.get();
  }
}
