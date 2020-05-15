package com.ht.project.snsproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FeedCacheServiceImpl implements FeedCacheService{

  private static final long DEPRECATED = 0;

  @Autowired
  FriendService friendService;

  @Autowired
  @Qualifier("cacheRedisTemplate")
  RedisTemplate<String, Object> cacheRedisTemplate;

  @Autowired
  @Qualifier("cacheStrRedisTemplate")
  StringRedisTemplate cacheStrRedisTemplate;

  @Resource(name = "cacheRedisTemplate")
  ZSetOperations<String, Object> zSetOps;

  @Resource(name = "cacheRedisTemplate")
  ValueOperations<String, Object> valueOps;

  public FeedInfo cacheToFeedInfo(int feedId, String userId, FriendStatus friendStatus) {

    String feedInfoKey = "feedInfo:"+feedId;

    ObjectMapper mapper = new ObjectMapper();

    String feedInfoStrCache = cacheStrRedisTemplate.boundValueOps(feedInfoKey).get();

    try {
      if (feedInfoStrCache != null) {

        FeedInfoCache feedInfoCache = mapper.readValue(feedInfoStrCache, FeedInfoCache.class);
        PublicScope publicScope = PublicScope.valueOf(feedInfoCache.getPublicScope());

        if(!friendService.checkPublicScopeByFriendStatus(publicScope,friendStatus)) {
          throw new InvalidApproachException("유효하지 않은 요청입니다.");
        }

        Double isGoodPushed = zSetOps.score("goodStatus:" + userId, feedId);

        boolean goodStatus = true;

        if (isGoodPushed == null || isGoodPushed == DEPRECATED) {
          goodStatus = false;
        }

        return FeedInfo.cacheToFeedInfo(feedInfoCache, goodStatus);
      }
    } catch (JsonProcessingException e) {
      throw new SerializationException("변환에 실패하였습니다.", e);
    }

    return null;
  }
}
