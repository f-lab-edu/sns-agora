package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.FeedInsert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedMapper {
    void feedUpload(FeedInsert feedInsert);
}
