package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.FeedInsert;
import com.ht.project.snsproject.model.feed.FeedList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedMapper {

    void feedUpload(FeedInsert feedInsert);

    List<FeedList> getFeedList(String userId, Pagination pagination);

}
