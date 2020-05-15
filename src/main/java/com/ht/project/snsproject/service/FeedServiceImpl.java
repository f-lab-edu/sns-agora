package com.ht.project.snsproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.mapper.FriendMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedDeleteParam;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import com.ht.project.snsproject.model.feed.FeedInsert;
import com.ht.project.snsproject.model.feed.FeedListParam;
import com.ht.project.snsproject.model.feed.FeedParam;
import com.ht.project.snsproject.model.feed.FeedUpdate;
import com.ht.project.snsproject.model.feed.FeedUpdateParam;
import com.ht.project.snsproject.model.feed.FeedVo;
import com.ht.project.snsproject.model.feed.FileVo;
import com.ht.project.snsproject.model.feed.FriendsFeedList;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@Service
public class FeedServiceImpl implements FeedService {

  private static final long DEPRECATED = 0;

  @Autowired
  FeedMapper feedMapper;

  @Autowired
  FriendMapper friendMapper;

  @Autowired
  @Qualifier("awsFileService")
  FileService fileService;

  @Autowired
  GoodService goodService;

  @Autowired
  FeedCacheService feedCacheService;

  @Autowired
  FriendService friendService;

  @Autowired
  @Qualifier("cacheRedisTemplate")
  RedisTemplate<String, Object> cacheRedisTemplate;

  @Resource(name = "cacheRedisTemplate")
  ZSetOperations<String, Object> zSetOps;

  @Resource(name = "cacheRedisTemplate")
  ValueOperations<String, Object> valueOps;


  @Transactional
  @Override
  public void feedUpload(List<MultipartFile> files, FeedVo feedVo, String userId) {

    Timestamp date = Timestamp.valueOf(LocalDateTime.now());
    FeedInsert feedInsert = FeedInsert.builder()
            .userId(userId)
            .title(feedVo.getTitle())
            .content(feedVo.getContent())
            .date(date)
            .publicScope(feedVo.getPublicScope())
            .good(0)
            .build();

    feedMapper.feedUpload(feedInsert);

    if (!files.isEmpty()) {
      fileService.fileUpload(files, userId, feedInsert.getId());
    }
  }

  @Transactional
  @Override
  public Feed getFeed(String userId, String targetId, int id) {

    Feed.FeedBuilder feedBuilder = Feed.builder();
    FeedInfo feedInfo = getFeedInfo(id, userId, targetId);

    String fileNames = feedInfo.getFileNames();
    int feedId = feedInfo.getId();
    int good = goodService.getGood(feedId);

    boolean goodStatus = feedInfo.isGoodStatus();

    if (goodStatus) {
      double time = Timestamp.valueOf(LocalDateTime.now()).getTime();
      zSetOps.add("goodStatus:"+userId, feedId, time);
      cacheRedisTemplate.expire("goodStatus:"+userId,
              cacheRedisTemplate.getExpire("userInfo:"+userId) + 60L,
              TimeUnit.SECONDS);//세션 만료 시간 + 60초로 설정 로그아웃 이후에도 한 번 더 batch insert를 해야하기 때문.
    }

    feedBuilder.id(feedId)
            .userId(feedInfo.getUserId())
            .title(feedInfo.getTitle())
            .content(feedInfo.getContent())
            .date(feedInfo.getDate())
            .publicScope(feedInfo.getPublicScope())
            .goodStatus(goodStatus)
            .good(good);

    if (fileNames != null) {

      String filePath = feedInfo.getPath();
      String[] fileNameArray = fileNames.split(",");
      List<FileVo> fileVoList = new ArrayList<>();
      int fileIndex = 0;

      for (String fileName:fileNameArray) {
        fileVoList.add(FileVo.getInstance(++fileIndex, filePath, fileName));
      }
      feedBuilder.files(fileVoList);
    }

    return feedBuilder.build();
  }

  @Transactional
  public FeedInfo getFeedInfo(int feedId, String userId, String targetId) {

    String key = "feedInfo:" + feedId;

    //친구 여부 확인
    FriendStatus friendStatus = friendService.getFriendStatus(userId, targetId);

    FeedInfo feedInfo = feedCacheService.cacheToFeedInfo(feedId, userId, friendStatus);

    if(feedInfo != null) {
      return feedInfo;
    }

    feedInfo = feedMapper.getFeed(FeedParam.create(feedId, userId, friendStatus));

    if (feedInfo == null || feedInfo.getId() == null) {
      throw new InvalidApproachException("일치하는 데이터가 없습니다.");
    }

    valueOps.set(key, FeedInfoCache.feedInfoToCache(feedInfo),5L, TimeUnit.MINUTES);

    return feedInfo;
  }

  /*  getFeedList() 메소드의 경우,
      targetId 에 해당하는 user 의 피드 목록을 조회해야하므로 순서와 데이터 정확도가 중요하기 때문에
      피드 전체를 캐시에서 확인하지 않고,
      good 수 증감은 redis 에서 저장하므로 캐시에서는 good 수만 체크하여 가져온다.
  */
  @Transactional
  @Override
  public List<Feed> getFeedList(String userId, String targetId, Pagination pagination) {

    if (userId.equals(targetId)) {
      return getFeeds(FeedListParam.create(targetId, pagination, PublicScope.ME));
    }

    FriendStatus friendStatus = friendMapper.getFriendRelationStatus(userId, targetId)
            .getFriendStatus();

    switch (friendStatus) {
      case FRIEND:
        return getFeeds(FeedListParam.create(targetId, pagination, PublicScope.FRIENDS));

      case BLOCK:
        throw new InvalidApproachException("유효하지 않은 접근입니다.");

      default:
        return getFeeds(FeedListParam.create(targetId, pagination, PublicScope.ALL));
    }
  }

  @Transactional
  @Override
  public List<Feed> getFeeds(FeedListParam feedListParam) {

    List<FeedInfo> feedInfoList = feedMapper.getFeedList(feedListParam);
    List<Feed> feeds = new ArrayList<>();
    String userId = feedListParam.getUserId();

    for (FeedInfo feedInfo:feedInfoList) {

      List<FileVo> files = new ArrayList<>();
      int feedId = feedInfo.getId();
      String feedCacheKey = "feedInfo:" + feedId;

      if(cacheRedisTemplate.hasKey(feedCacheKey)==null) {
        cacheRedisTemplate.opsForValue().set(feedCacheKey, feedInfo, 5L, TimeUnit.MINUTES);
      }
      int good = goodService.getGood(feedId);
      boolean goodStatus = feedInfo.isGoodStatus();

      if (goodStatus) {
        zSetOps.add("goodStatus:"+userId, feedId, Timestamp.valueOf(LocalDateTime.now()).getTime());
        cacheRedisTemplate.expire("goodStatus:"+userId,
                cacheRedisTemplate.getExpire("userInfo"+userId)+60L,
                TimeUnit.SECONDS);//세션 만료 시간과 일치
      }

      Feed.FeedBuilder builder = Feed.builder()
              .id(feedId)
              .userId(feedInfo.getUserId())
              .title(feedInfo.getTitle())
              .content(feedInfo.getContent())
              .date(feedInfo.getDate())
              .publicScope(feedInfo.getPublicScope())
              .good(good)
              .goodStatus(goodStatus);

      int fileIndex = 0;
      if (feedInfo.getFileNames() != null) {
        StringTokenizer st = new StringTokenizer(feedInfo.getFileNames(), ",");
        while (st.hasMoreTokens()) {
          FileVo tmpFile = FileVo.getInstance(++fileIndex, feedInfo.getPath(), st.nextToken());
          files.add(tmpFile);
        }
        builder.files(files);
      }
      Feed tmp = builder.build();
      feeds.add(tmp);
    }

    return feeds;
  }

  @Override
  public List<Feed> getFriendsFeedList(String userId, Pagination pagination) {

    List<FeedInfo> feedInfoList = feedMapper.getFriendsFeedList(
            FriendsFeedList.create(userId,pagination));
    List<Feed> feeds = new ArrayList<>();
    for (FeedInfo feedInfo:feedInfoList) {

      List<FileVo> files = new ArrayList<>();
      StringTokenizer st = new StringTokenizer(feedInfo.getFileNames(),",");
      int fileIndex = 0;
      int feedId = feedInfo.getId();
      int good = goodService.getGood(feedId);
      boolean goodStatus = feedInfo.isGoodStatus();

      if (goodStatus) {

        zSetOps.add("goodStatus:"+userId, feedId, Timestamp.valueOf(LocalDateTime.now()).getTime());
        cacheRedisTemplate.expire("goodStatus:"+userId,
                cacheRedisTemplate.getExpire("userInfo"+userId) + 60L,
                TimeUnit.SECONDS);
      }

      Feed.FeedBuilder builder = Feed.builder()
              .id(feedId)
              .userId(feedInfo.getUserId())
              .title(feedInfo.getTitle())
              .content(feedInfo.getContent())
              .date(feedInfo.getDate())
              .publicScope(feedInfo.getPublicScope())
              .good(good)
              .goodStatus(goodStatus);

      while (st.hasMoreTokens()) {
        FileVo tmpFile = FileVo.getInstance(++fileIndex,feedInfo.getPath(),st.nextToken());
        files.add(tmpFile);
      }

      builder.files(files);
      Feed tmp = builder.build();
      feeds.add(tmp);
    }

    return feeds;
  }


  @Transactional
  @Override
  public void deleteFeed(int id, String userId) {
    boolean result = feedMapper.deleteFeed(new FeedDeleteParam(id, userId));

    if (!result) {
      throw new InvalidApproachException("일치하는 데이터가 없습니다.");
    }
    fileService.deleteAllFiles(id);
  }

  @Transactional
  @Override
  public void updateFeed(List<MultipartFile> files,
                         FeedUpdateParam feedUpdateParam, int feedId, String userId) {

    Timestamp date = Timestamp.valueOf(LocalDateTime.now());
    FeedUpdate feedUpdate = FeedUpdate.builder()
            .id(feedId)
            .userId(userId)
            .title(feedUpdateParam.getTitle())
            .content(feedUpdateParam.getContent())
            .date(date)
            .publicScope(feedUpdateParam.getPublicScope())
            .build();

    boolean result = feedMapper.updateFeed(feedUpdate);
    if (!result) {
      throw new InvalidApproachException("일치하는 데이터가 없습니다.");
    }
    fileService.updateFiles(files,userId,feedId);
  }
}
