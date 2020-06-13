package com.ht.project.snsproject.enumeration;

public enum CacheKeyPrefix {
  FEED("feedInfo"), GOOD("good"), GOODPUSHED("goodPushed");

  private final String value;

  CacheKeyPrefix(String value) {
    this.value = value;
  }

  public static CacheKeyPrefix fromString(String value) {
    switch (value) {
      case "feedInfo": return FEED;
      case "good": return GOOD;
      case "goodPusehd": return GOODPUSHED;
      default: throw new IllegalArgumentException("Unknown value: " + value);
    }
  }
}
