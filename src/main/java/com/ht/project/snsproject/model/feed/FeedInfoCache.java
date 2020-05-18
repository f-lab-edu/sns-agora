package com.ht.project.snsproject.model.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedInfoCache {

  String id;

  String userId;

  String title;

  String content;

  String date;

  String publicScope;

  String path;

  String fileNames;

  public static FeedInfoCache from(FeedInfo feedInfo) {

    return FeedInfoCache.builder()
            .id(String.valueOf(feedInfo.getId()))
            .userId(feedInfo.getUserId())
            .title(feedInfo.getTitle())
            .content(feedInfo.getContent())
            .date(String.valueOf(feedInfo.getDate().getTime()))
            .publicScope(String.valueOf(feedInfo.getPublicScope()))
            .path(feedInfo.getPath())
            .fileNames(feedInfo.getFileNames()).build();
  }
}
