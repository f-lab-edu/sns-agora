package com.ht.project.snsproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.*;
import com.ht.project.snsproject.repository.comment.CommentRepository;
import com.ht.project.snsproject.repository.feed.FeedRepository;
import com.ht.project.snsproject.repository.good.GoodRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class FeedServiceImpl implements FeedService {

  private final FileService fileService;

  private final GoodRepository goodRepository;

  private final FriendService friendService;

  private final ObjectMapper cacheObjectMapper;

  private final FeedRepository feedRepository;

  private final CommentRepository commentRepository;

  private final RedisCacheService redisCacheService;

  public FeedServiceImpl(@Qualifier("awsFileService") FileService fileService,
                         GoodRepository goodRepository,
                         FriendService friendService,
                         @Qualifier("cacheObjectMapper") ObjectMapper cacheObjectMapper,
                         FeedRepository feedRepository,
                         CommentRepository commentRepository,
                         RedisCacheService redisCacheService) {
    this.fileService = fileService;
    this.goodRepository = goodRepository;
    this.friendService = friendService;
    this.cacheObjectMapper = cacheObjectMapper;
    this.feedRepository = feedRepository;
    this.commentRepository = commentRepository;
    this.redisCacheService = redisCacheService;
  }

  @Transactional
  @Override
  public void feedUpload(List<MultipartFile> files, FeedWriteDto feedWriteDto, String userId) {

    FeedInsert feedInsert = FeedInsert.create(feedWriteDto, userId, LocalDateTime.now());

    feedRepository.insertFeed(feedInsert);

    if (!files.isEmpty()) {

      fileService.uploadFiles(files, String.valueOf(feedInsert.getId()));

      fileService.insertFileInfoList(feedWriteDto.getFileDtoList(), feedInsert.getId());
    }
  }

  @Transactional(readOnly = true)
  @Override
  public List<Feed> findFriendsFeedListByUserId(String userId, Pagination pagination) {

    return findFeedList(userId, feedRepository.findFriendsFeedIdList(userId, pagination));
  }

  @Transactional(readOnly = true)
  @Override
  public List<Feed> findFeedListByUserId(String userId, String targetId, Pagination pagination) {

    List<Integer> feedIdList;

    switch (friendService.getFriendStatus(userId, targetId)) {

      case ME:
        feedIdList = feedRepository.findMyFeedIdListByUserId(targetId, pagination);
        break;

      case FRIEND:
        feedIdList = feedRepository.findFriendFeedIdListByUserId(targetId, pagination);
        break;

      case BLOCK:
        throw new InvalidApproachException("유효 하지 않은 접근입니다.");

      default:
        feedIdList = feedRepository.findALLFeedIdListByUserId(targetId, pagination);
    }

    return findFeedList(userId, feedIdList);
  }

  @Transactional(readOnly = true)
  private List<Feed> findFeedList(String userId, List<Integer> feedIdList) {

    List<Feed> feedList = new ArrayList<>();
    List<MultiSetTarget> multiSetTargetList = new ArrayList<>();
    List<FeedInfo> feedInfoList = feedRepository.findFeedInfoList(feedIdList, multiSetTargetList);
    Map<Integer, Boolean> goodPushedStatusMap =
            goodRepository.findGoodPushedStatusMap(userId, feedIdList, multiSetTargetList);

    Map<Integer, Integer> goodCountMap = goodRepository.findGoodCountMap(feedIdList, multiSetTargetList);
    Map<Integer, Integer> commentCountMap = commentRepository.findCommentCountMap(feedIdList, multiSetTargetList);

    feedInfoList.forEach(feedInfo -> {

      int feedId = feedInfo.getId();
      feedList.add(Feed.create(feedInfo, goodCountMap.get(feedId),
              commentCountMap.get(feedId), goodPushedStatusMap.get(feedId)));
    });

    redisCacheService.multiSet(multiSetTargetList);

    return feedList;
  }

  @Transactional(readOnly = true)
  @Override
  public Feed findFeedByFeedId(String userId, int feedId) {

    Object feed;
    FriendStatus friendStatus = friendService.findFriendStatus(feedId, userId);

    switch(friendStatus) {

      case ME:

        feed = feedRepository.findMyFeedByFeedId(feedId);
        break;

      case FRIEND:

        feed = feedRepository.findFriendsFeedByFeedId(feedId);
        break;

      default:

        feed = feedRepository.findAllFeedByFeedId(feedId);
    }

    FeedInfo feedInfo = cacheObjectMapper.convertValue(feed, FeedInfo.class);

    if(!isValidatedFeedInfo(feedInfo.getPublicScope(), friendStatus)) {

      throw new IllegalArgumentException("일치하는 피드가 존재하지 않습니다.");
    }

    return Feed.create(feedInfo, goodRepository.getGood(feedId),
            commentRepository.getCommentCount(feedId), goodRepository.isGoodPushed(feedId, userId));
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

  @Transactional
  @Override
  public void deleteFeed(int id, String userId) {

    boolean result = feedRepository.deleteFeed(id, userId);

    if (!result) {
      throw new InvalidApproachException("일치하는 데이터가 없습니다.");
    }

    fileService.deleteFiles(id);
  }

  @Transactional
  @Override
  public void updateFeed(List<MultipartFile> files,
                         FeedWriteDto feedWriteDto, int feedId, String userId) {

    boolean result = feedRepository.updateFeed(feedId, userId, feedWriteDto);

    if (!result) {
      throw new InvalidApproachException("일치하는 데이터가 없습니다.");
    }

    fileService.updateFiles(files,feedWriteDto.getFileDtoList(), feedId);
  }

}
