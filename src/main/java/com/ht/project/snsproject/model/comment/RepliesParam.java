package com.ht.project.snsproject.model.comment;

import lombok.Value;

@Value
public class RepliesParam {

  int commentId;

  Integer Cursor;

  int limit = 10;
}
