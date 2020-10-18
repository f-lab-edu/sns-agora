package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedRecommendMapper {

  List<FeedInfo> findLatestAllFeedList(FeedInfoParam feedInfoParam);
}
