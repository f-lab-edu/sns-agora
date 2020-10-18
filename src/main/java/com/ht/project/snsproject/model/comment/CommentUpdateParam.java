package com.ht.project.snsproject.model.comment;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;

@Value
@Builder
public class CommentUpdateParam {

  int commentId;

  String userId;

  String content;

  Timestamp updateTime;
}
