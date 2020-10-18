package com.ht.project.snsproject.model.good;

import com.ht.project.snsproject.enumeration.PublicScope;
import java.sql.Timestamp;

import com.ht.project.snsproject.model.feed.FeedInfo;
import com.ht.project.snsproject.model.feed.FeedInfoCache;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class FeedCacheUpdateParam {

  int feedId;

  String userId;

  String title;

  String content;

  Timestamp date;

  PublicScope publicScope;

  int good;

  public static FeedCacheUpdateParam create(FeedInfoCache feedInfoCache, int goodCache){

    return FeedCacheUpdateParam.builder()
            .feedId(Integer.parseInt(feedInfoCache.getId()))
            .userId(feedInfoCache.getUserId())
            .title(feedInfoCache.getTitle())
            .content(feedInfoCache.getContent())
            .date(new Timestamp(Long.parseLong(feedInfoCache.getDate())))
            .publicScope(PublicScope.valueOf(feedInfoCache.getPublicScope()))
            .good(goodCache)
            .build();
  }
}
