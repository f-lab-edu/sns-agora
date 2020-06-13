package com.ht.project.snsproject.model.comment;

import lombok.Value;

@Value
public class CommentsParam {

  int feedId;

  Integer cursor;

  int limit = 10;
}
