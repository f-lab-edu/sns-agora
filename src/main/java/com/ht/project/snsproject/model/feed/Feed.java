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
public class Feed {

  private Integer id;

  private String userId;

  private String title;

  private String content;

  private LocalDateTime date;

  private PublicScope publicScope;

  private boolean goodPushed;

  private int goodCount;

  private int commentCount;

  private List<FileVo> files;

  public static Feed create(FeedInfo feedInfo, boolean goodPushed) {

    return Feed.builder()
            .id(feedInfo.getId())
            .userId(feedInfo.getUserId())
            .title(feedInfo.getTitle())
            .content(feedInfo.getContent())
            .date(feedInfo.getDate())
            .publicScope(feedInfo.getPublicScope())
            .goodPushed(goodPushed)
            .goodCount(feedInfo.getGoodCount())
            .commentCount(feedInfo.getCommentCount())
            .files(feedInfo.getFiles())
            .build();
  }
}
