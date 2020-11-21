package com.ht.project.snsproject.model.good;

import lombok.Value;

import java.util.List;

@Value
public class GoodPushedStatusListParam {

  String userId;

  List<Integer> feedIdList;
}
