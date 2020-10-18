package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;

@Builder
@Value
@AllArgsConstructor
public class FeedInfo {

  Integer id;

  String userId;

  String title;

  String content;

  Timestamp date;

  PublicScope publicScope;

  String filePath;

  String fileNames;

  boolean goodPushed;

  public static FeedInfo from(FeedInfoCache feedInfoCache, boolean goodPushed){

    return FeedInfo.builder().id(Integer.parseInt(feedInfoCache.getId()))
            .userId(feedInfoCache.getUserId())
            .title(feedInfoCache.getTitle())
            .content(feedInfoCache.getContent())
            .date(new Timestamp(Long.parseLong(feedInfoCache.getDate())))
            .publicScope(PublicScope.valueOf(feedInfoCache.getPublicScope()))
            .filePath(feedInfoCache.getFilePath())
            .fileNames(feedInfoCache.getFileNames())
            .goodPushed(goodPushed)
            .build();
  }
}
