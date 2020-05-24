package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feed {

  int id;

  String userId;

  String title;

  String content;

  Timestamp date;

  PublicScope publicScope;

  int good;

  boolean goodPushed;

  List<FileVo> files;

  public static Feed create(FeedInfo feedInfo, int good, boolean goodPushed, List<FileVo> files) {

    return Feed.builder().id(feedInfo.getId())
            .userId(feedInfo.getUserId())
            .title(feedInfo.getTitle())
            .content(feedInfo.getContent())
            .date(feedInfo.getDate())
            .publicScope(feedInfo.getPublicScope())
            .good(good)
            .goodPushed(goodPushed)
            .files(files).build();
  }
}
