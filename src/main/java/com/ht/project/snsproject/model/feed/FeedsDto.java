package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class FeedsDto {

  Integer id;

  String userId;

  String title;

  String content;

  Timestamp date;

  PublicScope publicScope;

  boolean goodPushed;

  int goodCount;

  int commentCount;

  List<Files> files;
}
