package com.ht.project.snsproject.model.good;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class GoodPushedStatusesParam {

  List<Integer> feedIds;

  String userId;
}
