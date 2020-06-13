package com.ht.project.snsproject.model.comment;

import lombok.Value;

@Value
public class ReplysParam {

  int commentId;

  Integer Cursor;

  int limit = 10;
}
