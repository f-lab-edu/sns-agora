package com.ht.project.snsproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class FeedServiceImpl implements FeedService {

  @Autowired
  private FeedMapper feedMapper;

  @Autowired
  @Qualifier("awsFileService")
  private FileService fileService;

  @Autowired
  private GoodService goodService;

  @Autowired
  private FeedCacheService feedCacheService;

  @Autowired
  private FriendService friendService;

  @Autowired
  @Qualifier("cacheObjectMapper")
  private ObjectMapper cacheObjectMapper;

  @Transactional
  @Override
  public void feedUpload(List<MultipartFile> files, FeedWriteDto feedWriteDto, String userId) {

    FeedInsert feedInsert = FeedInsert.create(feedWriteDto, userId, LocalDateTime.now());

    feedMapper.feedUpload(feedInsert);

    if (!files.isEmpty()) {
      fileService.fileUpload(files, userId, feedInsert.getId());
    }
  }

  @Transactional(readOnly = true)
  @Override
  public List<Feed> findFriendsFeedListByUserId(String userId, Pagination pagination) {

    List<Feed> feeds = feedMapper.findFriendsFeedListByUserId(new FeedInfoParam(userId, pagination));

    if (!feeds.isEmpty()) {

      feedCacheService.setFeedListCache(feeds, userId, 60L);
    }

    return feeds;
  }

  @Transactional
  @Override
  public List<Feed> findFeedListByUserId(String userId, String targetId, Pagination pagination) {

    List<Feed> feedList;

    switch (friendService.getFriendStatus(userId, targetId)) {

      case ME:
        feedList = findMyFeedListByUserId(userId, targetId, pagination);
        break;

      case FRIEND:
        feedList = findFriendFeedListByUserId(userId, targetId, pagination);
        break;

      case BLOCK:
        throw new InvalidApproachException("유효 하지 않은 접근입니다.");

      default:
        feedList = findAllFeedListByUserId(userId, targetId, pagination);
    }

    feedCacheService.setFeedListCache(feedList, userId, 60L);

    return feedList;
  }

  @Transactional(readOnly = true)
  private List<Feed> findMyFeedListByUserId(String userId, String targetId, Pagination pagination) {

    return feedMapper.findMyFeedListByUserId(new TargetFeedsParam(userId, targetId, pagination));
  }

  @Transactional(readOnly = true)
  private List<Feed> findAllFeedListByUserId(String userId, String targetId, Pagination pagination) {

    return feedMapper.findAllFeedListByUserId(new TargetFeedsParam(userId, targetId, pagination));
  }


  @Transactional(readOnly = true)
  private List<Feed> findFriendFeedListByUserId(String userId, String targetId, Pagination pagination) {

    return feedMapper.findFriendFeedListByUserId(new TargetFeedsParam(userId, targetId, pagination));
  }


  @Transactional
  @Override
  public Feed findFeedByFeedId(String userId, String targetId, int feedId) {

    Object feed;
    FriendStatus friendStatus = friendService.getFriendStatus(userId, targetId);

    switch(friendStatus) {

      case ME:

        feed = feedCacheService.findMyFeedByFeedId(feedId, targetId, userId);
        break;

      case FRIEND:

        feed = feedCacheService.findFriendsFeedByFeedId(feedId, targetId, userId);
        break;

      default:

        feed = feedCacheService.findAllFeedByFeedId(feedId, targetId, userId);
    }

    FeedInfo feedInfo = cacheObjectMapper.convertValue(feed, FeedInfo.class);

    if(!isValidatedFeedInfo(feedInfo.getPublicScope(), friendStatus)) {

      throw new IllegalArgumentException("일치하는 피드가 존재하지 않습니다.");
    }

    return Feed.create(feedInfo, goodService.isGoodPushed(feedId, userId));
  }

  private boolean isValidatedFeedInfo(PublicScope publicScope, FriendStatus friendStatus) {

    switch (publicScope) {

      case ME:
        return friendStatus == FriendStatus.ME;

      case FRIENDS:
        return friendStatus == FriendStatus.ME || friendStatus == FriendStatus.FRIEND;

      default:
        return true;
    }
  }

  @Override
  public List<FeedInfo> findFeedListByFeedIdList(List<Integer> recommendIdx) {
    return feedMapper.findFeedListByFeedIdList(recommendIdx);
  }

  @Transactional
  @Override
  public void deleteFeed(int id, String userId) {
    boolean result = feedCacheService.deleteFeed(id, userId);

    if (!result) {
      throw new InvalidApproachException("일치하는 데이터가 없습니다.");
    }

    fileService.deleteAllFiles(id);
  }

  @Transactional
  @Override
  public void updateFeed(List<MultipartFile> files,
                         FeedWriteDto feedWriteDto, int feedId, String userId) {

    boolean result = feedCacheService.updateFeed(feedId, userId, feedWriteDto);

    if (!result) {
      throw new InvalidApproachException("일치하는 데이터가 없습니다.");
    }

    fileService.updateFiles(files,userId,feedId);
  }

}
