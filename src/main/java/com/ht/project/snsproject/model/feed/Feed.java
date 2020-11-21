package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class Feed {

  Integer id;

  String userId;

  String title;

  String content;

  LocalDateTime date;

  PublicScope publicScope;

  boolean goodPushed;

  int goodCount;

  int commentCount;

  List<FileVo> files;


  public static Feed create(FeedInfo feedInfo, int goodCount, int commentCount, boolean goodPushed) {

    return Feed.builder()
            .id(feedInfo.getId())
            .userId(feedInfo.getUserId())
            .title(feedInfo.getTitle())
            .content(feedInfo.getContent())
            .date(feedInfo.getDate())
            .publicScope(feedInfo.getPublicScope())
            .goodPushed(goodPushed)
            .goodCount(goodCount)
            .commentCount(commentCount)
            .files(feedInfo.getFiles())
            .build();
  }
}
