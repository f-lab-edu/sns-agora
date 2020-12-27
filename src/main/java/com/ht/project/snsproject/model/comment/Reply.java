package com.ht.project.snsproject.model.comment;

import lombok.NoArgsConstructor;
import lombok.Value;

import java.sql.Timestamp;

@Value
@NoArgsConstructor(force = true)
public class Reply {

  int id;

  String userId;

  String content;

  Timestamp writeTime;

}
