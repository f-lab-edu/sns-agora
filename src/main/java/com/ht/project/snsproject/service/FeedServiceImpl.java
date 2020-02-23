package com.ht.project.snsproject.service;

import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.FeedInsert;
import com.ht.project.snsproject.model.feed.FeedList;
import com.ht.project.snsproject.model.feed.FeedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedServiceImpl implements FeedService{

    @Autowired
    FeedMapper feedMapper;

    @Override
    public int feedUpload(FeedVO feedVo, String userId) {

        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        FeedInsert.FeedInsertBuilder builder = FeedInsert.builder();
        builder.userId(userId);
        builder.title(feedVo.getTitle());
        builder.content(feedVo.getContent());
        builder.date(date);
        builder.publicScope(feedVo.getPublicScope());
        builder.recommend(0);

        FeedInsert feedInsert = builder.build();
        feedMapper.feedUpload(feedInsert);

        return feedInsert.getId();
    }

    @Override
    public List<FeedList> getFeedList(String userId, Pagination pagination) {
        return feedMapper.getFeedList(userId, pagination);
    }

}
