package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.FeedInfoCache;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedRecommendCacheMapper {

  List<FeedInfoCache> getFeedInfoByLatestOrder();
}
