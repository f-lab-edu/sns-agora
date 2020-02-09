package com.ht.project.snsproject.service;

import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.model.feed.FeedInsert;
import com.ht.project.snsproject.model.feed.FeedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class FeedServiceImpl implements FeedService{

    @Autowired
    FeedMapper feedMapper;

    @Override
    public void feedUpload(FeedVO feedVo, String userId, String path) {
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        FeedInsert feedInsert = new FeedInsert(userId,
                feedVo.getTitle(),
                feedVo.getContent(),
                path,
                date,
                feedVo.getPublicScope());

        feedMapper.feedUpload(feedInsert);
    }
}
