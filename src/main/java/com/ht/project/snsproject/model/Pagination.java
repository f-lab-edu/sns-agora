package com.ht.project.snsproject.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Pagination {

  Integer cursor;

  int listSize = 10;

  public static Pagination pageInfo(Integer cursor) {

    return Pagination.builder()
            .cursor(cursor).build();
  }
}
