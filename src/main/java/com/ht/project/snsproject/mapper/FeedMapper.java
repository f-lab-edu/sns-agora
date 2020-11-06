package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedMapper {

  void feedUpload(FeedInsert feedInsert);

  boolean deleteFeed(FeedDeleteParam feedDeleteParam);

  boolean updateFeed(FeedUpdate feedUpdate);

  List<Feed> findFriendsFeedListByUserId(FeedInfoParam feedInfoParam);

  FeedInfo findMyFeedByFeedId(FeedParam feedParam);

  FeedInfo findFriendsFeedByFeedId(FeedParam feedParam);

  FeedInfo findAllFeedByFeedId(FeedParam feedParam);

  List<Feed> findAllFeedListByUserId(TargetFeedsParam targetFeedsParam);

  List<Feed> findFriendFeedListByUserId(TargetFeedsParam targetFeedsParam);

  List<Feed> findMyFeedListByUserId(TargetFeedsParam targetFeedsParam);

  List<FeedInfo> findFeedListByFeedIdList(List<Integer> recommendIdx);
}
