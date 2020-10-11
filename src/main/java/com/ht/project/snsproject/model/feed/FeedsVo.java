package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;
import java.util.List;

@Value
@Builder
public class FeedsVo {

  Integer id;

  String userId;

  String title;

  String content;

  Timestamp date;

  PublicScope publicScope;

  boolean goodPushed;

  int goodCount;

  int commentCount;

  List<Files> files;

  public static FeedsVo create(FeedsInfo feedsInfo, boolean goodPushed) {

    return FeedsVo.builder()
            .id(feedsInfo.getId())
            .userId(feedsInfo.getUserId())
            .title(feedsInfo.getTitle())
            .content(feedsInfo.getContent())
            .date(feedsInfo.getDate())
            .publicScope(feedsInfo.getPublicScope())
            .goodPushed(goodPushed)
            .goodCount(feedsInfo.getGoodCount())
            .commentCount(feedsInfo.getCommentCount())
            .files(feedsInfo.getFiles())
            .build();
  }

  public static FeedsVo create(FeedsDto feedsDto) {

    return FeedsVo.builder()
            .id(feedsDto.getId())
            .userId(feedsDto.getUserId())
            .title(feedsDto.getTitle())
            .content(feedsDto.getContent())
            .date(feedsDto.getDate())
            .publicScope(feedsDto.getPublicScope())
            .goodPushed(feedsDto.isGoodPushed())
            .goodCount(feedsDto.getGoodCount())
            .commentCount(feedsDto.getCommentCount())
            .files(feedsDto.getFiles())
            .build();
  }
}
