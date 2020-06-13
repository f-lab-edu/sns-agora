package com.ht.project.snsproject.model.good;

import java.util.List;
import lombok.Value;

@Value
public class GoodUsersParam {

  int feedId;

  List<Object> recommendUsers;
}
