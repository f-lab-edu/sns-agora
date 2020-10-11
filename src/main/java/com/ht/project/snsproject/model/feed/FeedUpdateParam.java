package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class FeedUpdateParam {

  @NotNull String title;

  String content;

  @NotNull PublicScope publicScope;

}
