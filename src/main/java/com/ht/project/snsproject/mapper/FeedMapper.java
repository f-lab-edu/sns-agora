package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.FeedInsert;
import com.ht.project.snsproject.model.feed.FeedList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedMapper {

    void feedUpload(FeedInsert feedInsert);

    List<FeedList> getFeedList(@Param("userId") String userId, @Param("pagination") Pagination pagination);

}
