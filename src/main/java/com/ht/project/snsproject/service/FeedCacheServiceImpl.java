package com.ht.project.snsproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.model.feed.*;
import com.ht.project.snsproject.model.good.GoodPushedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ht.project.snsproject.quartz.FeedRecommendCacheService.RECOMMEND_LIST;
import static com.ht.project.snsproject.service.RedisCacheService.FEED_INFO_CACHE_PREFIX;
import static com.ht.project.snsproject.service.RedisCacheService.SPRING_CACHE_PREFIX;

/**
 * FeedCacheService 를 인터페이스를 구현하여 전략 패턴으로 디자인한 이유
 * 현재는 해당 프로젝트에서 레디스를 활용하여 캐시를 사용하고 있습니다.
 * 하지만 이 후에 레디스 보다 더 나은 방법의 캐시 저장소를 발견한다면 교체를 해야할 수 있습니다.
 * 만약 해당 서비스 강결합 되어 있다면, 서비스의 필요한 기능들을 하나하나 다시 정의하고 구현해야만 합니다.
 * 이를 인터페이스로 구현하면 정의되어 있는 기능을 구현하기만 하면 됩니다.
 * 즉, 이 후 기술변화에 대한 서비스 계층의 코드가 영향을 받지 않토록 결합도를 낮추기 위해서
 * 해당 서비스를 인터페이스를 통하여 추상화하였습니다.
 */
@Service
public class FeedCacheServiceImpl implements FeedCacheService{

  @Autowired
  private FriendService friendService;

  /*
  @Resource 를 통한 빈 주입이 아닌 @Qualifier 를 통한 부가정보를 사용한 이유
  - @Resource 를 활용할 때, 빈 이름은 변경되기가 쉽고 그 자체로 의미 부여가 쉽지 않음.
  - 빈 이름과는 별도로 추가적인 메타정보를 지정해서 의미를 부여해놓고 @Autowired에서 사용할 수 있도록 하는
    @Autowired 가 훨씬 직관적이고 깔끔하다.
  - 아래와 같이 선언시, cacheRedisTemplate 이라는 한정자 값을 가진 빈으로 자동와이어링 대상을 제한할 수 있다.
   */
  @Resource(name = "cacheRedisTemplate")
  private ListOperations<String, Object> listOps;

  @Resource(name = "cacheRedisTemplate")
  private ValueOperations<String, Object> valueOps;

  @Autowired
  private RedisCacheService redisCacheService;

  @Autowired
  private FeedService feedService;

  @Autowired
  private FeedMapper feedMapper;

  @Autowired
  @Qualifier("cacheObjectMapper")
  private ObjectMapper objectMapper;

  @Override
  public void setFeedInfoCache(List<FeedInfo> feedInfoList, long expire) {

    List<MultiSetTarget> multiSetTargetList = new ArrayList<>();

    for (FeedInfo feedInfo : feedInfoList) {

      int feedId = feedInfo.getId();
      String key = SPRING_CACHE_PREFIX + FEED_INFO_CACHE_PREFIX + feedId;

      try {

        multiSetTargetList.add(MultiSetTarget.builder()
                .key(key)
                .target(objectMapper.writeValueAsString(feedInfo))
                .expire(expire)
                .build());

      } catch (JsonProcessingException e) {
        throw new SerializationException("변환에 실패하였습니다.", e);
      }
    }

    redisCacheService.multiSet(multiSetTargetList);
  }

  @Transactional
  @Override
  public List<FeedInfo> getLatestALLFeedList() {

    List<Integer> recommendIdx =
            Objects.requireNonNull(
                    listOps.range(RECOMMEND_LIST, 0, -1),
                    "추천 목록이 존재하지 않습니다.")
                    .stream()
                    .map(s->(Integer) s)
                    .collect(Collectors.toList());

    List<String> feedKeyList = redisCacheService.makeMultiKeyList(CacheKeyPrefix.FEED, recommendIdx);
    List<FeedInfo> feedInfoList = findRecommendFeedListInCache(
            valueOps.multiGet(feedKeyList),
            recommendIdx);


    if(!recommendIdx.isEmpty()) {

      feedInfoList.addAll(feedService.findFeedListByFeedIdList(recommendIdx));
    }

    return feedInfoList;
  }

  /**
   * 캐시에서 가져온 추천목록을 통해
   * 캐시에서 추천 피드를 조회합니다.
   * 피드를 가져올 때마다 추천목록에서 해당 피드의 번호를 제거합니다.
   * ※ Integer 데이터지만 Integer로 감싼 이유는
   * 인덱스 조회가 아닌 value 조회를 위해서 입니다.
   * @param cacheList
   * @param recommendIdx
   * @return
   */
  private List<FeedInfo> findRecommendFeedListInCache(List<Object> cacheList,
                                                      List<Integer> recommendIdx) {

    List<FeedInfo> feedInfoList = new ArrayList<>();

    if(cacheList!=null) {

      for (Object cache : cacheList) {

        if (cache != null) {
          FeedInfo feedInfo = objectMapper.convertValue(cache, FeedInfo.class);
          feedInfoList.add(feedInfo);
          recommendIdx.remove(Integer.valueOf(feedInfo.getId()));
        }
      }
    }

    return feedInfoList;
  }

  @Transactional
  @Override
  public void setFeedListCache(List<Feed> feedList, String userId, long expire) {

    List<FeedInfo> feedInfoList = new ArrayList<>();
    List<GoodPushedStatus> goodPushedStatusList = new ArrayList<>();

    for(Feed feedDto : feedList) {

      feedInfoList.add(FeedInfo.from(feedDto));
      goodPushedStatusList.add(new GoodPushedStatus(feedDto.getId(),feedDto.isGoodPushed()));
    }

    setFeedInfoCache(feedInfoList, expire);
    multiSetGoodPushedStatus(goodPushedStatusList, userId, expire);
  }

  @Transactional
  private void multiSetGoodPushedStatus(List<GoodPushedStatus> goodPushedStatusList,
                                       String userId,
                                        long expire) {

    List<MultiSetTarget> multiSetTargetList = new ArrayList<>();

    for (GoodPushedStatus goodPushedStatus :  goodPushedStatusList) {

      int feedId = goodPushedStatus.getFeedId();
      String key = redisCacheService.makeCacheKey(CacheKeyPrefix.GOOD_PUSHED, feedId, userId);

      multiSetTargetList.add(MultiSetTarget.builder()
              .key(key)
              .target(String.valueOf(goodPushedStatus.getPushedStatus()))
              .expire(expire)
              .build());
    }

    redisCacheService.multiSet(multiSetTargetList);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "feedInfo", key = "'feedInfo:' + #feedId")
  public Object findMyFeedByFeedId(int feedId, String targetId, String userId) {

    FeedInfo feed = feedMapper.findMyFeedByFeedId(new FeedParam(feedId, targetId, userId));

    if(feed.getId() == null) {

      throw new IllegalArgumentException("일치하는 데이터가 존재하지 않습니다.");
    }

    return feed;
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "feedInfo", key = "'feedInfo:' + #feedId")
  public Object findFriendsFeedByFeedId(int feedId, String targetId, String userId) {

    FeedInfo feed = feedMapper.findFriendsFeedByFeedId(new FeedParam(feedId, targetId, userId));

    if(feed.getId() == null) {

      throw new IllegalArgumentException("일치하는 데이터가 존재하지 않습니다.");
    }

    return feed;
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "feedInfo", key = "'feedInfo:' + #feedId")
  public Object findAllFeedByFeedId(int feedId, String targetId, String userId) {

    FeedInfo feed = feedMapper.findAllFeedByFeedId(new FeedParam(feedId, targetId, userId));

    if(feed.getId() == null) {

      throw new IllegalArgumentException("일치하는 데이터가 존재하지 않습니다.");
    }

    return feed;
  }

  @Override
  @Transactional
  @CacheEvict(value = "feedInfo", key = "'feedInfo:' + #feedId")
  public boolean deleteFeed(int feedId, String userId) {

    return feedMapper.deleteFeed(new FeedDeleteParam(feedId, userId));
  }

  @Override
  @Transactional
  @CacheEvict(value = "feedInfo", key = "'feedInfo:' + #feedId")
  public boolean updateFeed(int feedId, String userId, FeedWriteDto feedWriteDto) {

    return feedMapper.updateFeed(FeedUpdate.create(feedId, userId, feedWriteDto, LocalDateTime.now()));
  }
}
