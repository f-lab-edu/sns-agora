package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@AllArgsConstructor
public class FeedInfo {

  int id;

  String userId;

  String title;

  String content;

  Timestamp date;

  PublicScope publicScope;

  String path;

  String fileNames;

  public static FeedInfo cacheToFeedInfo(FeedInfoCache feedInfoCache){

    return FeedInfo.builder().id(Integer.parseInt(feedInfoCache.getId()))
            .userId(feedInfoCache.getUserId())
            .title(feedInfoCache.getTitle())
            .content(feedInfoCache.getContent())
            .date(new Timestamp(Long.parseLong(feedInfoCache.getDate())))
            .publicScope(PublicScope.valueOf(feedInfoCache.getPublicScope()))
            .path(feedInfoCache.getPath())
            .fileNames(feedInfoCache.getFileNames())
            .build();
  }
}
