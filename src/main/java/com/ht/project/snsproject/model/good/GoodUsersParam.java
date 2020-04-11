package com.ht.project.snsproject.model.good;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class GoodUsersParam {

  int feedId;

  String userId;
}
