package com.ht.project.snsproject.model.good;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GoodUser {

  int feedId;

  String userId;
}
