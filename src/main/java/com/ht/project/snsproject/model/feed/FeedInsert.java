package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
public class FeedInsert {

  int id;

  String userId;

  String title;

  String content;

  LocalDateTime date;

  PublicScope publicScope;

  int good;

  public static FeedInsert create(FeedWriteDto feedWriteDto, String userId, LocalDateTime date) {
    return FeedInsert.builder()
            .userId(userId)
            .title(feedWriteDto.getTitle())
            .content(feedWriteDto.getContent())
            .date(date)
            .publicScope(feedWriteDto.getPublicScope())
            .good(0)
            .build();
  }
}
