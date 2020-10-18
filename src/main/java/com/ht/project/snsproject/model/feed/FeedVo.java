package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import lombok.Value;

@Value
public class FeedVo {

  String title;

  String content;

  PublicScope publicScope;
}
