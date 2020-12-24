package com.ht.project.snsproject.model.feed;

import com.google.firebase.database.annotations.NotNull;
import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedWriteDto {

  @NotNull
  private String title;

  private String content;

  private List<FileDto> fileDtoList;

  @NotNull
  private PublicScope publicScope;
}
