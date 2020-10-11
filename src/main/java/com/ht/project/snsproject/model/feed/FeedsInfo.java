package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class FeedsInfo {

  Integer id;

  String userId;

  String title;

  String content;

  Timestamp date;

  PublicScope publicScope;

  int goodCount;

  int commentCount;

  List<Files> files;
}
