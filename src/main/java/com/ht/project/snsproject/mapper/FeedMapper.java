package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.FeedDeleteParam;
import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInsert;
import com.ht.project.snsproject.model.feed.FeedListParam;
import com.ht.project.snsproject.model.feed.FeedParam;
import com.ht.project.snsproject.model.feed.FeedUpdate;
import com.ht.project.snsproject.model.feed.FriendsFeedList;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedMapper {

  void feedUpload(FeedInsert feedInsert);

  FeedInfo getFeed(FeedParam feedParam);

  FeedInfo getFeedInfoCache(int feedId);

  List<FeedInfo> getFeedList(FeedListParam feedListParam);

  List<FeedInfo> getFriendsFeedList(FriendsFeedList friendsFeedList);

  boolean deleteFeed(FeedDeleteParam feedDeleteParam);

  boolean updateFeed(FeedUpdate feedUpdate);
}
