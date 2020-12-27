package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedMapper {

  void feedUpload(FeedInsert feedInsert);

  boolean deleteFeed(FeedDeleteParam feedDeleteParam);

  boolean updateFeed(FeedUpdate feedUpdate);

  FeedInfo findMyFeedByFeedId(int feedId);

  FeedInfo findFriendsFeedByFeedId(int feedId);

  FeedInfo findAllFeedByFeedId(int feedId);

  List<Integer> findMyFeedIdListByUserId(FeedIdListParam feedIdListParam);

  List<Integer> findFriendFeedIdListByUserId(FeedIdListParam feedIdListParam);

  List<Integer> findAllFeedIdListByUserId(FeedIdListParam feedIdListParam);

  List<FeedInfo> findFeedInfoListByFeedIdList(List<Integer> feedIdList);

  List<Integer> findFriendsFeedIdList(FriendsFeedIdParam friendsFeedIdParam);
}
