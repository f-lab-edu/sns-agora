package com.ht.project.snsproject.enumeration;

public enum ReadStatus {

  READ(1), NO_READ(2);

  private final int value;

  ReadStatus(int value) {
    this.value = value;
  }

  public static ReadStatus valueOf(int value) {
    switch (value) {
      case 1: return READ;
      case 2: return NO_READ;
      default: throw new AssertionError("Unknown value" + value);
    }
  }
}
