package com.ht.project.snsproject.model.comment;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;

@Value
@Builder
public class ReplyUpdateParam {

  int replyId;

  String userId;

  String content;

  Timestamp updateTime;
}
