package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
public class FeedUpdate {

  int id;

  String userId;

  String title;

  String content;

  LocalDateTime date;

  PublicScope publicScope;

  public static FeedUpdate create (int feedId, String userId, FeedWriteDto feedUpdateParam, LocalDateTime date) {

    return FeedUpdate.builder()
            .id(feedId)
            .userId(userId)
            .title(feedUpdateParam.getTitle())
            .content(feedUpdateParam.getContent())
            .date(date)
            .publicScope(feedUpdateParam.getPublicScope())
            .build();
  }
}
