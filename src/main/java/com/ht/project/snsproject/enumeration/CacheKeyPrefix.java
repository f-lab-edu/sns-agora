package com.ht.project.snsproject.enumeration;

public enum CacheKeyPrefix {
  FEED("feedInfo"), GOOD("good"), GOOD_PUSHED("goodPushed"),
  COMMENT_COUNT("commentCount"), USER_INFO("userInfo");

  private final String value;

  CacheKeyPrefix(String value) {
    this.value = value;
  }

  public static CacheKeyPrefix fromString(String value) {
    switch (value) {
      case "feedInfo": return FEED;
      case "good": return GOOD;
      case "goodPusehd": return GOOD_PUSHED;
      case "commentCount": return COMMENT_COUNT;
      case "userInfo": return USER_INFO;
      default: throw new IllegalArgumentException("Unknown value: " + value);
    }
  }
}
