package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedMapper {

  void feedUpload(FeedInsert feedInsert);

  FeedInfo getFeed(FeedParam feedParam);

  List<FeedInfo> getFeedList(FeedListParam feedListParam);

  List<FeedInfo> getFriendsFeedList(FriendsFeedList friendsFeedList);

  boolean deleteFeed(FeedDeleteParam feedDeleteParam);

  boolean updateFeed(FeedUpdate feedUpdate);

  List<FeedsDto> findFriendsFeedListByUserId(FeedsParam feedsParam);

  FeedsInfo findMyFeedByFeedId(FeedDtoParam feedDtoParam);

  FeedsInfo findFriendsFeedByFeedId(FeedDtoParam feedDtoParam);

  FeedsInfo findAllFeedByFeedId(FeedDtoParam feedDtoParam);

  List<FeedsDto> findAllFeedListByUserId(TargetFeedsParam targetFeedsParam);

  List<FeedsDto> findFriendFeedListByUserId(TargetFeedsParam targetFeedsParam);

  List<FeedsDto> findMyFeedListByUserId(TargetFeedsParam targetFeedsParam);
}
