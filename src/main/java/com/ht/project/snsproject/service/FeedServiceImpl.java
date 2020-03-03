package com.ht.project.snsproject.service;

import com.ht.project.snsproject.Exception.InvalidApproachException;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.mapper.FeedMapper;
import com.ht.project.snsproject.mapper.FriendMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.feed.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class FeedServiceImpl implements FeedService{

    @Autowired
    FeedMapper feedMapper;

    @Autowired
    FriendMapper friendMapper;

    @Autowired
    @Qualifier("localFileService")
    FileService fileService;

    @Autowired
    FeedService feedService;

    @Transactional
    @Override
    public void feedUpload(List<MultipartFile> files, FeedVO feedVo, String userId) {

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
        fileService.fileUpload(files, userId, feedInsert.getId());
    }

    @Transactional
    @Override
    public List<Feed> getFeed(String userId, String targetId, int id) {

        if(userId.equals(targetId)){
            return feedMapper.getFeed(FeedParam.create(id, targetId, FriendStatus.ME));
        }

        FriendStatus friendStatus = friendMapper.getFriendRelationStatus(userId, targetId).getFriendStatus();

        if(friendStatus == FriendStatus.BLOCK){
            throw new InvalidApproachException("유효하지 않은 접근입니다.");
        }

        List<Feed> feed =feedMapper.getFeed(FeedParam.create(id, targetId, friendStatus));

        if(feed.isEmpty()){
            throw new InvalidApproachException("일치하는 데이터가 없습니다.");
        }

        return feed;
    }

    @Transactional
    @Override
    public List<Feed> getFeedList(String userId, String targetId, Pagination pagination) {

        if(userId.equals(targetId)){
            return feedService.getFeeds(FeedListParam.create(targetId, pagination, PublicScope.ME));
        }

        FriendStatus friendStatus = friendMapper.getFriendRelationStatus(userId, targetId).getFriendStatus();

        switch (friendStatus){
            case FRIEND:
                return feedService.getFeeds(FeedListParam.create(targetId, pagination, PublicScope.FRIENDS));

            case BLOCK:
                throw new InvalidApproachException("유효하지 않은 접근입니다.");

            default:
                return feedService.getFeeds(FeedListParam.create(targetId, pagination, PublicScope.ALL));
        }

    }

    @Override
    public List<Feed> getFeeds(FeedListParam feedListParam) {
        List<FeedList> feedList = feedMapper.getFeedList(feedListParam);
        List<Feed> feeds = new ArrayList<>();
        for(FeedList feed:feedList){
            Feed.FeedBuilder builder = Feed.builder();
            List<FileVo> files = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(feed.getFileNames(),",");
            int fileIndex = 0;
            builder.id(feed.getId());
            builder.userId(feed.getUserId());
            builder.title(feed.getTitle());
            builder.content(feed.getContent());
            builder.date(feed.getDate());
            builder.publicScope(feed.getPublicScope());
            builder.recommend(feed.getRecommend());
            while(st.hasMoreTokens()){
                FileVo tmpFile = FileVo.getInstance(++fileIndex,feed.getPath(),st.nextToken());
                files.add(tmpFile);
            }
            builder.files(files);
            Feed tmp = builder.build();
            feeds.add(tmp);
        }
        return feeds;
    }

    @Override
    public List<Feed> getFriendsFeedList(String userId, Pagination pagination) {
        List<FeedList> feedList = feedMapper.getFriendsFeedList(FriendsFeedList.create(userId,pagination));
        List<Feed> feeds= new ArrayList<>();
        for (FeedList feed:feedList) {
            Feed.FeedBuilder builder = Feed.builder();
            List<FileVo> files = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(feed.getFileNames(),",");
            int fileIndex = 0;
            builder.id(feed.getId());
            builder.userId(feed.getUserId());
            builder.title(feed.getTitle());
            builder.content(feed.getContent());
            builder.date(feed.getDate());
            builder.publicScope(feed.getPublicScope());
            builder.recommend(feed.getRecommend());
            while(st.hasMoreTokens()){
                FileVo tmpFile = FileVo.getInstance(++fileIndex,feed.getPath(),st.nextToken());
                files.add(tmpFile);
            }
            builder.files(files);
            Feed tmp = builder.build();
            feeds.add(tmp);
        }

        return feeds;
    }

    @Transactional
    @Override
    public void deleteFeed(int id, String userId) {
        boolean result = feedMapper.deleteFeed(new FeedDeleteParam(id, userId));

        if(!result){
            throw new InvalidApproachException("일치하는 데이터가 없습니다.");
        }
        fileService.deleteFile(id);
    }

    @Transactional
    @Override
    public void updateFeed(List<MultipartFile> files, FeedUpdateParam feedUpdateParam, int feedId, String userId) {
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        FeedInsert.FeedInsertBuilder builder = FeedInsert.builder();
        builder.id(feedId);
        builder.userId(userId);
        builder.title(feedUpdateParam.getTitle());
        builder.content(feedUpdateParam.getContent());
        builder.date(date);
        builder.publicScope(feedUpdateParam.getPublicScope());
        builder.recommend(feedUpdateParam.getRecommend());
        FeedInsert feedInsert = builder.build();

        boolean result = feedMapper.updateFeed(feedInsert);
        if(!result){
            throw new InvalidApproachException("일치하는 데이터가 없습니다.");
        }
        fileService.deleteFile(feedId);
        fileService.fileUpload(files, userId, feedId);
    }
}
