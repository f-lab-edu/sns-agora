package com.ht.project.snsproject.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecommendMapper {

    List<String> getRecommendList(int feedId);

    int getRecommend(int feed);

    void initRecommendList(int feedId);
}
