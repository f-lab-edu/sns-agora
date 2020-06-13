package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;
import java.util.List;

@Value
@Builder
public class RecommendFeed {

  int id;

  String userId;

  String title;

  String content;

  Timestamp date;

  PublicScope publicScope;

  int good;

  List<FileVo> files;

  public static RecommendFeed from(RecommendFeedInfo recommendFeedInfo, int good, List<FileVo> files) {

    return RecommendFeed.builder().id(recommendFeedInfo.getId())
            .userId(recommendFeedInfo.getUserId())
            .title(recommendFeedInfo.getTitle())
            .content(recommendFeedInfo.getContent())
            .date(recommendFeedInfo.getDate())
            .publicScope(recommendFeedInfo.getPublicScope())
            .good(good).files(files).build();
  }
}
