package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.mapper.CommentMapper;
import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.mapper.GoodMapper;
import com.ht.project.snsproject.model.feed.Feed;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.good.GoodStatusParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestService {

  private final FeedMapper feedMapper;

  private final GoodMapper goodMapper;

  private final FriendService friendService;

  private final CommentMapper commentMapper;

  @Transactional(readOnly = true)
  public Feed findFeedByFeedId(String userId, int feedId) {

    FeedInfo feedInfo;
    FriendStatus friendStatus = friendService.findFriendStatus(feedId, userId);

    switch(friendStatus) {

      case ME:

        feedInfo = feedMapper.findMyFeedByFeedId(feedId);
        break;

      case FRIEND:

        feedInfo = feedMapper.findFriendsFeedByFeedId(feedId);
        break;

      default:

        feedInfo = feedMapper.findAllFeedByFeedId(feedId);
    }

    if(feedInfo == null) {

      throw new IllegalArgumentException("일치하는 피드가 존재하지 않습니다.");
    }

    return Feed.create(feedInfo, goodMapper.getGood(feedId),
            commentMapper.getCommentCount(feedId),
            goodMapper.getGoodPushedStatus(new GoodStatusParam(feedId, userId)));
  }
}
