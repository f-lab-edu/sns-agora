package com.ht.project.snsproject.enumeration;

public enum CacheKeyPrefix {
  FEED(1), GOOD(2), GOODPUSHED(3);

  private final int value;

  CacheKeyPrefix(int value) {
    this.value = value;
  }

  public static CacheKeyPrefix valueOf(int value) {
    switch (value) {
      case 1: return FEED;
      case 2: return GOOD;
      case 3: return GOODPUSHED;
      default: throw new AssertionError("Unknown value: " + value);
    }
  }
}
