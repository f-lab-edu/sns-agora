package com.ht.project.snsproject.model.feed;

import com.google.firebase.database.annotations.NotNull;
import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Value;

@Value
public class FeedWriteDto {

  @NotNull
  String title;

  String content;

  @NotNull
  PublicScope publicScope;
}
