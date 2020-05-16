package com.ht.project.snsproject.enumeration;

public enum GoodStatus {
  DEPRECATED(0), PUSHED(1), NOT_PUSHED(2);

  private final int value;

  GoodStatus (int value) {
    this.value = value;
  }

  public static GoodStatus valueOf(int value) {
    switch (value) {
      case 0: return DEPRECATED;
      case 1: return PUSHED;
      case 2: return NOT_PUSHED;
      default: throw new AssertionError("Unknown value: " + value);
    }
  }
}
