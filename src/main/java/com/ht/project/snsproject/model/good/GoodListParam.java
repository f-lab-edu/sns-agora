package com.ht.project.snsproject.model.good;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GoodListParam {

  int feedId;

  Integer cursor;

  int limit = 10;
}
