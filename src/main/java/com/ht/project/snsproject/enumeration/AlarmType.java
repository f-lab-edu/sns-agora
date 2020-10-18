package com.ht.project.snsproject.enumeration;

public enum AlarmType {

  FRIEND_REQ(1), FRIEND_RES(2), COMMENT(3), LIKE(4);

  private final int value;

  AlarmType(int value) {
    this.value = value;
  }

  public static AlarmType valueOf(int value) {
    switch (value) {
      case 1: return FRIEND_REQ;
      case 2: return FRIEND_RES;
      case 3: return COMMENT;
      case 4: return LIKE;
      default: throw new AssertionError("Unknown value: " + value);
    }
  }
}
