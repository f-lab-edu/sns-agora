package com.ht.project.snsproject.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedRecommendCacheMapper {

  List<Integer> getFeedInfoByLatestOrder();
}
