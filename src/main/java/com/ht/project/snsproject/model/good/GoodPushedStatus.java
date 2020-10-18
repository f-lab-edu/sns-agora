package com.ht.project.snsproject.model.good;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GoodPushedStatus {

  Integer feedId;

  Boolean pushedStatus;
}
