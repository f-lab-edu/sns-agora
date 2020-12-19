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

  private List<FileVo> files;

}
