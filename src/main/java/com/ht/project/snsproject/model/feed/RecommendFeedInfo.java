package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;

@Value
@Builder
public class RecommendFeedInfo {

  Integer id;

  String userId;

  String title;

  String content;

  Timestamp date;

  PublicScope publicScope;

  String filePath;

  String fileNames;

  public static RecommendFeedInfo from(FeedInfoCache feedInfoCache) {

    return RecommendFeedInfo.builder()
            .id(Integer.parseInt(feedInfoCache.getId()))
            .userId(feedInfoCache.getUserId())
            .title(feedInfoCache.getTitle())
            .content(feedInfoCache.getContent())
            .date(Timestamp.valueOf(feedInfoCache.getDate()))
            .publicScope(PublicScope.valueOf(feedInfoCache.getPublicScope()))
            .filePath(feedInfoCache.getFilePath())
            .fileNames(feedInfoCache.getFileNames()).build();
  }
}
