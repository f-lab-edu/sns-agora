package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.mapper.FriendMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.*;
import com.ht.project.snsproject.model.good.GoodPushedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;


@Service
public class FeedServiceImpl implements FeedService {

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


  @Transactional
  @Override
  public void feedUpload(List<MultipartFile> files, FeedVo feedVo, String userId) {

    Timestamp date = Timestamp.valueOf(LocalDateTime.now());
    FeedInsert feedInsert = FeedInsert.create(feedVo, userId, date);

    feedMapper.feedUpload(feedInsert);

    if (!files.isEmpty()) {
      fileService.fileUpload(files, userId, feedInsert.getId());
    }
  }

  @Transactional
  @Override
  public Feed getFeed(String userId, String targetId, int id) {

    FeedInfo feedInfo = getFeedInfo(id, userId, targetId);
    List<FileVo> files = getFileList(feedInfo.getFileNames(), feedInfo.getPath());

    int feedId = feedInfo.getId();
    int good = goodService.getGood(feedId);

    boolean goodPushed = feedInfo.isGoodPushed();

    return Feed.create(feedInfo, good, goodPushed, files);

  }

  @Transactional
  public FeedInfo getFeedInfo(int feedId, String userId, String targetId) {

    //친구 여부 확인
    FriendStatus friendStatus = friendService.getFriendStatus(userId, targetId);

    FeedInfo feedInfo = feedCacheService.getFeedInfoFromCache(feedId, userId, friendStatus);

    if(feedInfo != null) {
      return feedInfo;
    }

    feedInfo = feedMapper.getFeed(FeedParam.create(feedId, userId, friendStatus));

    if (feedInfo == null || feedInfo.getId() == null) {
      throw new InvalidApproachException("일치하는 데이터가 없습니다.");
    }

    feedCacheService.addFeedInfoToCache(FeedInfoCache.from(feedInfo), 60L, TimeUnit.SECONDS);

    return feedInfo;
  }

  /*  getFeedList() 메소드의 경우,
      targetId 에 해당하는 user 의 피드 목록을 조회해야하므로 순서와 데이터 정확도가 중요하기 때문에
      피드 전체를 캐시에서 확인하지 않고,
      good 수 증감은 redis 에서 저장하므로 캐시에서는 good 수만 체크하여 가져온다.
  */
  @Transactional
  @Override
  public List<Feed> getFeedListByUser(String userId, String targetId, Pagination pagination) {

    if (userId.equals(targetId)) {
      return getFeedsByUser(FeedListParam.create(userId, targetId, pagination, PublicScope.ME));
    }

    FriendStatus friendStatus = friendMapper.getFriendRelationStatus(userId, targetId)
            .getFriendStatus();

    switch (friendStatus) {
      case FRIEND:
        return getFeedsByUser(FeedListParam.create(userId, targetId, pagination, PublicScope.FRIENDS));

      case BLOCK:
        throw new InvalidApproachException("유효하지 않은 접근입니다.");

      default:
        return getFeedsByUser(FeedListParam.create(userId, targetId, pagination, PublicScope.ALL));
    }
  }

  @Transactional
  @Override
  public List<Feed> getFeedsByUser(FeedListParam feedListParam) {

    return getFeeds(getFeedInfoListByUser(feedListParam), feedListParam.getUserId());

  }

  public List<FeedInfo> getFeedInfoListByUser(FeedListParam feedListParam) {

    return feedMapper.getFeedList(feedListParam);
  }

  @Transactional
  @Override
  public List<Feed> getFriendsFeedList(String userId, Pagination pagination) {

    return getFeeds(getFeedInfoListByFriends(FriendsFeedList.create(userId, pagination)), userId);

  }

  public List<FeedInfo> getFeedInfoListByFriends (FriendsFeedList friendsFeedList) {

    return feedMapper.getFriendsFeedList(friendsFeedList);
  }

  public List<FileVo> getFileList(String fileNames, String path) {

    List<FileVo> files = new ArrayList<>();
    int fileIndex = 0;

    if (fileNames != null) {

      StringTokenizer st = new StringTokenizer(fileNames, ",");

      while (st.hasMoreTokens()) {

        FileVo file = FileVo.getInstance(++fileIndex, path, st.nextToken());
        files.add(file);
      }
    } else {
      files = null;
    }

    return files;
  }

  public List<Integer> getFeedIds(List<FeedInfo> feedInfoList) {

    List<Integer> feedIds = new ArrayList<>();

    for(FeedInfo feedInfo: feedInfoList) {
      int feedId = feedInfo.getId();

      feedIds.add(feedId);
    }

    return feedIds;
  }



  public List<Feed> getFeeds(List<FeedInfo> feedInfoList, String userId) {

    List<Integer> feedIds = getFeedIds(feedInfoList);
    Map<Integer, Boolean> goodPushedMap = goodService.getGoodPushedStatusesFromCache(feedIds, userId);
    Map<Integer, Integer> goodsMap = goodService.getGoods(feedIds);
    List<Feed> feeds = new ArrayList<>();
    List<GoodPushedStatus> goodPushedStatuses = new ArrayList<>();
    List<FeedInfoCache> feedInfoCacheList = new ArrayList<>();

    for(FeedInfo feedInfo: feedInfoList) {

      int feedId = feedInfo.getId();
      List<FileVo> files = getFileList(feedInfo.getFileNames(), feedInfo.getPath());
      Boolean goodPushed = goodPushedMap.get(feedId);
      feedInfoCacheList.add(FeedInfoCache.from(feedInfo));

      if(goodPushed == null) {
        goodPushed = feedInfo.isGoodPushed();
      }

      goodPushedStatuses.add(GoodPushedStatus.builder()
              .feedId(feedId)
              .pushedStatus(goodPushed)
              .build());

      Integer good = goodsMap.get(feedId);

      feeds.add(Feed.create(feedInfo, good, goodPushed, files));
    }

    feedCacheService.pipelining(goodPushedStatuses, userId, 60L);
    feedCacheService.pipeliningFeedInfoCache(feedInfoCacheList,60L);

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

    boolean result = feedMapper.updateFeed(FeedUpdate.create(feedId, userId, feedUpdateParam, date));
    if (!result) {
      throw new InvalidApproachException("일치하는 데이터가 없습니다.");
    }
    fileService.updateFiles(files,userId,feedId);
  }
}
