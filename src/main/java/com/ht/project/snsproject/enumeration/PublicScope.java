package com.ht.project.snsproject.enumeration;

public enum PublicScope {
  ALL(1), FRIENDS(2), ME(3);

  private final int value;

  PublicScope(int value) {
    this.value = value;
  }

  public static PublicScope valueOf(int value) {
    switch (value) {
      case 1: return ALL;
      case 2: return FRIENDS;
      case 3: return ME;
      default: throw new AssertionError("Unknown value: " + value);
    }
  }
}
