package com.ht.project.snsproject.model.feed;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MultiSetTarget {

  String key;

  String target;

  long expire;
}
