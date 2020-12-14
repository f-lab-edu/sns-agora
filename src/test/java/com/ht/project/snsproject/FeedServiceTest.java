package com.ht.project.snsproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.*;
import com.ht.project.snsproject.service.FeedCacheService;
import com.ht.project.snsproject.service.FeedServiceImpl;
import com.ht.project.snsproject.service.FriendService;
import com.ht.project.snsproject.service.GoodService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

  @Mock
  private FeedCacheService feedCacheService;

  @Mock
  private FeedMapper feedMapper;

  @Mock
  private FriendService friendService;

  @Mock
  private GoodService goodService;

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
            .goodCount(0)
            .commentCount(0)
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.ALL)
            .files(files).build();

    when(friendService.getFriendStatus(userId, targetId))
            .thenReturn(FriendStatus.FRIEND);

    when(feedCacheService.findFriendsFeedByFeedId(feedId, targetId, userId))
            .thenReturn(feedInfo);

    when(goodService.isGoodPushed(feedId, userId)).thenReturn(false);

    when(cacheObjectMapper.convertValue(feedInfo, FeedInfo.class))
            .thenReturn(feedInfo);

    Feed feed = feedService.findFeedByFeedId(userId, targetId, feedId);

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
            .goodCount(0)
            .commentCount(0)
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.ALL)
            .files(files).build();

    when(friendService.getFriendStatus(userId, targetId))
            .thenReturn(FriendStatus.ME);

    when(feedCacheService.findMyFeedByFeedId(feedId, targetId, userId))
            .thenReturn(feedInfo);

    when(goodService.isGoodPushed(feedId, userId)).thenReturn(false);

    when(cacheObjectMapper.convertValue(feedInfo, FeedInfo.class))
            .thenReturn(feedInfo);

    Feed feed = feedService.findFeedByFeedId(userId, targetId, feedId);

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
            .goodCount(0)
            .commentCount(0)
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.ALL)
            .files(files).build();

    when(friendService.getFriendStatus(userId, targetId))
            .thenReturn(FriendStatus.NONE);

    when(feedCacheService.findAllFeedByFeedId(feedId, targetId, userId))
            .thenReturn(feedInfo);

    when(goodService.isGoodPushed(feedId, userId)).thenReturn(false);

    when(cacheObjectMapper.convertValue(feedInfo, FeedInfo.class))
            .thenReturn(feedInfo);

    Feed feed = feedService.findFeedByFeedId(userId, targetId, feedId);

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
            .goodCount(0)
            .commentCount(0)
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.FRIENDS)
            .files(files).build();

    when(friendService.getFriendStatus(userId, targetId))
            .thenReturn(FriendStatus.NONE);

    when(feedCacheService.findAllFeedByFeedId(feedId, targetId, userId))
            .thenReturn(feedInfo);

    when(cacheObjectMapper.convertValue(feedInfo, FeedInfo.class))
            .thenReturn(feedInfo);

    assertThrows(IllegalArgumentException.class,
            () -> ReflectionTestUtils.invokeMethod(feedService,
                    "findFeedByFeedId",
                    userId, targetId, feedId));

  }

  @Test
  @DisplayName("userId를 가진 사용자의 피드 목록을 조회합니다.")
  public void findFeedListByUserIdShouldPass() {

    Pagination pagination = new Pagination(null);
    List<Feed> feedList = new ArrayList<>();
    feedList.add(Feed.builder().id(1)
            .userId(targetId)
            .title("test title")
            .content("test content")
            .goodCount(0)
            .commentCount(0)
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.FRIENDS)
            .goodPushed(false)
            .files(new ArrayList<>()).build());
    feedList.add(new Feed());
    feedList.add(new Feed());

    when(friendService.getFriendStatus(userId, targetId)).thenReturn(FriendStatus.FRIEND);
    when(feedMapper.findFriendFeedListByUserId(new TargetFeedsParam(userId, targetId, pagination)))
            .thenReturn(feedList);

    doNothing().when(feedCacheService).setFeedListCache(feedList, userId, 60L);

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
    List<Feed> feedList = new ArrayList<>();
    feedList.add(Feed.builder().id(1)
            .userId(targetId)
            .title("test title")
            .content("test content")
            .goodCount(0)
            .commentCount(0)
            .date(LocalDateTime.of(2020, 11, 13, 0, 0))
            .publicScope(PublicScope.FRIENDS)
            .goodPushed(false)
            .files(new ArrayList<>()).build());
    feedList.add(new Feed());
    feedList.add(new Feed());

    when(feedMapper.findFriendsFeedListByUserId(new FeedInfoParam(userId, pagination)))
            .thenReturn(feedList);

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
