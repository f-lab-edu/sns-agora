package com.ht.project.snsproject.model.recommend;

import java.util.List;
import lombok.Value;

@Value
public class RecommendUsersParam {

  int feedId;

  List<Object> recommendUsers;
}
