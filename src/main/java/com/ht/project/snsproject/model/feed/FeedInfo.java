package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedInfo {

  private Integer id;

  private String userId;

  private String title;

  private String content;

  private LocalDateTime date;

  private PublicScope publicScope;

  private int goodCount;

  private int commentCount;

  private List<FileVo> files;


  public static FeedInfo from(Feed feed) {

    return FeedInfo.builder()
            .id(feed.getId())
            .userId(feed.getUserId())
            .title(feed.getTitle())
            .content(feed.getContent())
            .date(feed.getDate())
            .publicScope(feed.getPublicScope())
            .goodCount(feed.getGoodCount())
            .commentCount(feed.getCommentCount())
            .files(feed.getFiles())
            .build();
  }
}
