package com.ht.project.snsproject.model.alarm;

import lombok.Value;

@Value
public class AlarmRead {

  int id;

  String userId;

  public static AlarmRead create(int id, String userId) {
    return new AlarmRead(id, userId);
  }
}
