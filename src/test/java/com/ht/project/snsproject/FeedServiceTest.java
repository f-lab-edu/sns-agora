package com.ht.project.snsproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FileVo;
import com.ht.project.snsproject.model.feed.MultiSetTarget;
import com.ht.project.snsproject.repository.comment.CommentRepository;
import com.ht.project.snsproject.repository.feed.FeedRepository;
import com.ht.project.snsproject.repository.good.GoodRepository;
import com.ht.project.snsproject.service.FeedServiceImpl;
import com.ht.project.snsproject.service.FriendService;
import com.ht.project.snsproject.service.RedisCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

  @Mock
  private FeedRepository feedRepository;

  @Mock
  private RedisCacheService redisCacheService;

  @Mock
  private FriendService friendService;

  @Mock
  private GoodRepository goodRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private ObjectMapper cacheObjectMapper;

  @InjectMocks
  private FeedServiceImpl feedService;

  private final String userId = "testUser1";
  private final String targetId = "testUser2";

  @Test
  @DisplayName("user 와 target이 친구관계일 때, 단일 피드 조회 성공합니다.")
  public void findFeedByFeedIdWhenFriendStatusIsFriendsShouldPass() {

    int feedId = 1;
    List<FileVo> files = new ArrayList<>();

    FeedInfo feedInfo = FeedInfo.builder()
            .id(feedId)
            .userId(targetId)
            .title("test title")
            .content("test content")
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.ALL)
            .files(files).build();

    when(friendService.findFriendStatus(feedId, userId))
            .thenReturn(FriendStatus.FRIEND);

    when(feedRepository.findFriendsFeedByFeedId(feedId))
            .thenReturn(feedInfo);

    when(goodRepository.getGood(feedId)).thenReturn(0);
    when(commentRepository.getCommentCount(feedId)).thenReturn(0);
    when(goodRepository.isGoodPushed(feedId, userId)).thenReturn(false);
    when(cacheObjectMapper.convertValue(feedInfo, FeedInfo.class))
            .thenReturn(feedInfo);

    Feed feed = feedService.findFeedByFeedId(userId, feedId);

    assertEquals(1, feed.getId());
    assertEquals(targetId, feed.getUserId());
    assertEquals("test title", feed.getTitle());
    assertEquals("test content", feed.getContent());
    assertEquals(0, feed.getGoodCount());
    assertEquals(0, feed.getCommentCount());
    assertEquals(LocalDateTime.of(2020, 11, 13, 0,0), feed.getDate());
    assertEquals(PublicScope.ALL, feed.getPublicScope());
    assertFalse(feed.isGoodPushed());
    assertTrue(feed.getFiles().isEmpty());

  }

  @Test
  @DisplayName("user 와 target이 동일할 때, 단일 피드 조회 성공합니다.")
  public void findFeedByFeedIdWhenFriendStatusIsMeShouldPass() {

    int feedId = 1;
    List<FileVo> files = new ArrayList<>();

    FeedInfo feedInfo = FeedInfo.builder()
            .id(feedId)
            .userId(targetId)
            .title("test title")
            .content("test content")
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.ALL)
            .files(files).build();

    when(friendService.findFriendStatus(feedId, userId))
            .thenReturn(FriendStatus.ME);

    when(feedRepository.findMyFeedByFeedId(feedId))
            .thenReturn(feedInfo);

    when(goodRepository.getGood(feedId)).thenReturn(0);
    when(commentRepository.getCommentCount(feedId)).thenReturn(0);
    when(goodRepository.isGoodPushed(feedId, userId)).thenReturn(false);

    when(cacheObjectMapper.convertValue(feedInfo, FeedInfo.class))
            .thenReturn(feedInfo);

    Feed feed = feedService.findFeedByFeedId(userId, feedId);

    assertEquals(1, feed.getId());
    assertEquals(targetId, feed.getUserId());
    assertEquals("test title", feed.getTitle());
    assertEquals("test content", feed.getContent());
    assertEquals(0, feed.getGoodCount());
    assertEquals(0, feed.getCommentCount());
    assertEquals(LocalDateTime.of(2020, 11, 13, 0,0), feed.getDate());
    assertEquals(PublicScope.ALL, feed.getPublicScope());
    assertFalse(feed.isGoodPushed());
    assertTrue(feed.getFiles().isEmpty());

  }

  @Test
  @DisplayName("user 와 target의 관계가 없을 때, 단일 피드 조회 성공합니다.")
  public void findFeedByFeedIdWhenFriendStatusIsNoneShouldPass() {

    int feedId = 1;
    List<FileVo> files = new ArrayList<>();

    FeedInfo feedInfo = FeedInfo.builder()
            .id(feedId)
            .userId(targetId)
            .title("test title")
            .content("test content")
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.ALL)
            .files(files).build();

    when(friendService.findFriendStatus(feedId, userId))
            .thenReturn(FriendStatus.NONE);

    when(feedRepository.findAllFeedByFeedId(feedId))
            .thenReturn(feedInfo);

    when(goodRepository.getGood(feedId)).thenReturn(0);
    when(commentRepository.getCommentCount(feedId)).thenReturn(0);
    when(goodRepository.isGoodPushed(feedId, userId)).thenReturn(false);

    when(cacheObjectMapper.convertValue(feedInfo, FeedInfo.class))
            .thenReturn(feedInfo);

    Feed feed = feedService.findFeedByFeedId(userId, feedId);

    assertEquals(1, feed.getId());
    assertEquals(targetId, feed.getUserId());
    assertEquals("test title", feed.getTitle());
    assertEquals("test content", feed.getContent());
    assertEquals(0, feed.getGoodCount());
    assertEquals(0, feed.getCommentCount());
    assertEquals(LocalDateTime.of(2020, 11, 13, 0,0), feed.getDate());
    assertEquals(PublicScope.ALL, feed.getPublicScope());
    assertFalse(feed.isGoodPushed());
    assertTrue(feed.getFiles().isEmpty());

  }

  @Test
  @DisplayName("친구 관계에 따른 피드 읽기 권한이 유효하지 않으면 IllegalArgumentException 발생합니다.")
  public void findFeedByFeedIdWithInvalidatedScopeThrowsException() {

    int feedId = 1;
    List<FileVo> files = new ArrayList<>();

    FeedInfo feedInfo = FeedInfo.builder()
            .id(feedId)
            .userId(targetId)
            .title("test title")
            .content("test content")
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.FRIENDS)
            .files(files).build();

    when(friendService.findFriendStatus(feedId, userId))
            .thenReturn(FriendStatus.NONE);

    when(feedRepository.findAllFeedByFeedId(feedId))
            .thenReturn(feedInfo);

    when(cacheObjectMapper.convertValue(feedInfo, FeedInfo.class))
            .thenReturn(feedInfo);

    assertThrows(IllegalArgumentException.class,
            () -> ReflectionTestUtils.invokeMethod(feedService,
                    "findFeedByFeedId",
                    userId, feedId));

  }

  @Test
  @DisplayName("userId를 가진 사용자의 피드 목록을 조회합니다.")
  public void findFeedListByUserIdShouldPass() {

    Pagination pagination = new Pagination(null);
    List<MultiSetTarget> multiSetTargetList = new ArrayList<>();
    List<FeedInfo> feedInfoList = new ArrayList<>();
    List<Integer> feedIdList = new ArrayList<>();
    Map<Integer, Boolean> goodPushedStatusMap = new HashMap<>();
    goodPushedStatusMap.put(1, false);
    Map<Integer, Integer> goodCountMap = new HashMap<>();
    goodCountMap.put(1, 0);
    Map<Integer, Integer> commentCountMap = new HashMap<>();
    commentCountMap.put(1, 0);

    feedInfoList.add(FeedInfo.builder().id(1)
            .userId(targetId)
            .title("test title")
            .content("test content")
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.FRIENDS)
            .files(new ArrayList<>()).build());

    when(friendService.getFriendStatus(userId, targetId)).thenReturn(FriendStatus.FRIEND);
    when(feedRepository.findFriendFeedIdListByUserId(targetId, pagination)).thenReturn(feedIdList);
    when(feedRepository.findFeedInfoList(feedIdList, multiSetTargetList)).thenReturn(feedInfoList);
    when(goodRepository.findGoodPushedStatusMap(userId, feedIdList, multiSetTargetList))
            .thenReturn(goodPushedStatusMap);
    when(goodRepository.findGoodCountMap(feedIdList, multiSetTargetList)).thenReturn(goodCountMap);
    when(commentRepository.findCommentCountMap(feedIdList, multiSetTargetList)).thenReturn(commentCountMap);

    List<Feed> result = feedService.findFeedListByUserId(userId, targetId, pagination);

    assertEquals(1, result.get(0).getId());
    assertEquals(targetId, result.get(0).getUserId());
    assertEquals("test title", result.get(0).getTitle());
    assertEquals("test content", result.get(0).getContent());
    assertEquals(0, result.get(0).getGoodCount());
    assertEquals(0, result.get(0).getCommentCount());
    assertEquals(LocalDateTime.of(2020, 11, 13, 0, 0),
            result.get(0).getDate());
    assertEquals(PublicScope.FRIENDS, result.get(0).getPublicScope());
    assertFalse(result.get(0).isGoodPushed());
    assertTrue(result.get(0).getFiles().isEmpty());
  }

  @Test
  @DisplayName("target 사용자가 user를 차단한 상태면 target 사용자 목록 조회시 Exception 발생합니다.")
  public void findFeedListByBlockUserIdThrowsException() {

    Pagination pagination = new Pagination(null);

    when(friendService.getFriendStatus(userId, targetId)).thenReturn(FriendStatus.BLOCK);

    assertThrows(InvalidApproachException.class,
            () -> ReflectionTestUtils.invokeMethod(feedService,
                    "findFeedListByUserId",
                    userId, targetId, pagination));

  }

  @Test
  @DisplayName("친구관계인 user들의 피드(팔로우한 사용자 피드) 조회를 성공합니다.")
  public void findFriendsFeedListByUserIdShouldPass() {

    Pagination pagination = new Pagination(null);
    List<Integer> feedIdList = new ArrayList<>();
    List<FeedInfo> feedInfoList = new ArrayList<>();
    List<MultiSetTarget> multiSetTargetList = new ArrayList<>();
    Map<Integer, Boolean> goodPushedStatusMap = new HashMap<>();
    goodPushedStatusMap.put(1, false);
    Map<Integer, Integer> goodCountMap = new HashMap<>();
    goodCountMap.put(1, 0);
    Map<Integer, Integer> commentCountMap = new HashMap<>();
    commentCountMap.put(1, 0);

    feedInfoList.add(FeedInfo.builder().id(1)
            .userId(targetId)
            .title("test title")
            .content("test content")
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.FRIENDS)
            .files(new ArrayList<>()).build());

    when(feedRepository.findFriendsFeedIdList(userId, pagination))
            .thenReturn(feedIdList);
    when(feedRepository.findFeedInfoList(feedIdList, multiSetTargetList)).thenReturn(feedInfoList);
    when(goodRepository.findGoodPushedStatusMap(userId, feedIdList, multiSetTargetList))
            .thenReturn(goodPushedStatusMap);
    when(goodRepository.findGoodCountMap(feedIdList, multiSetTargetList)).thenReturn(goodCountMap);
    when(commentRepository.findCommentCountMap(feedIdList, multiSetTargetList)).thenReturn(commentCountMap);
    Mockito.doNothing().when(redisCacheService).multiSet(multiSetTargetList);

    List<Feed> result = feedService.findFriendsFeedListByUserId(userId, pagination);

    assertEquals(1, result.get(0).getId());
    assertEquals(targetId, result.get(0).getUserId());
    assertEquals("test title", result.get(0).getTitle());
    assertEquals("test content", result.get(0).getContent());
    assertEquals(0, result.get(0).getGoodCount());
    assertEquals(0, result.get(0).getCommentCount());
    assertEquals(LocalDateTime.of(2020, 11, 13, 0, 0),
            result.get(0).getDate());
    assertEquals(PublicScope.FRIENDS, result.get(0).getPublicScope());
    assertFalse(result.get(0).isGoodPushed());
    assertTrue(result.get(0).getFiles().isEmpty());
  }
}
