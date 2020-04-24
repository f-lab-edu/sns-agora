package com.ht.project.snsproject.model.good;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class GoodUsersParam {

  int feedId;

  String userId;
}
