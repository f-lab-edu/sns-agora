package com.ht.project.snsproject.model;

import lombok.Value;

@Value
public class Pagination {

  Integer cursor;

  int listSize = 10;

  public static Pagination pageInfo(Integer cursor) {

    return new Pagination(cursor);
  }
}
