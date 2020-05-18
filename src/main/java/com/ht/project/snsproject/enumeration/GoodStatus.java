package com.ht.project.snsproject.enumeration;

public enum GoodStatus {
  DEPRECATED("deprecated"), PUSHED("pushed"), NOT_PUSHED("notPushed");

  private final String value;

  GoodStatus (String value) {
    this.value = value;
  }

  public static GoodStatus fromString(String value) {

    switch (value) {
      case "deprecated":
        return DEPRECATED;
      case "pushed":
        return PUSHED;
      case "notPushed":
        return NOT_PUSHED;
      default:
        throw new IllegalArgumentException("Unknown value: " + value);
    }

  }
}
