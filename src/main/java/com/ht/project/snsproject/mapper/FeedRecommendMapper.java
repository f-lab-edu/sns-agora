package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.FeedsInfo;
import com.ht.project.snsproject.model.feed.FeedsParam;
import com.ht.project.snsproject.model.feed.RecommendFeedInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedRecommendMapper {

  List<RecommendFeedInfo> getFeedRecommendListByLatestOrder(Pagination pagination);

  List<FeedsInfo> findLatestAllFeedList(FeedsParam feedsParam);
}
