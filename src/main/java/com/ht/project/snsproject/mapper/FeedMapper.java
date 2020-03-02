package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedMapper {

    void feedUpload(FeedInsert feedInsert);

    List<Feed> getFeed(FeedParam feedParam);

    List<FeedList> getFeedList(FeedListParam feedListParam);

    List<FeedList> getFriendsFeedList(FriendsFeedList friendsFeedList);

    boolean deleteFeed(FeedDeleteParam feedDeleteParam);
}
