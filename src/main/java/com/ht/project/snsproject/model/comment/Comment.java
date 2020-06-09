package com.ht.project.snsproject.model.comment;

import lombok.Value;

import java.sql.Timestamp;

@Value
public class Comment {

  int feedId;

  String userId;

  String content;

  Timestamp date;

  String replyUserId;

  String replyContent;

  Timestamp replyWriteTime;

}
