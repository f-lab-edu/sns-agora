package com.ht.project.snsproject.model.good;

import com.ht.project.snsproject.enumeration.PublicScope;
import java.sql.Timestamp;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class FeedCacheUpdateParam {

  int feedId;

  String userId;

  String title;

  String content;

  Timestamp date;

  PublicScope publicScope;

  int good;
}
