package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.model.Pagination;
import lombok.Value;

@Value
public class FeedListParam {

  String userId;

  Pagination pagination;

  PublicScope publicScope;

  public static FeedListParam create(String userId,
                                     Pagination pagination, PublicScope publicScope) {

    return new FeedListParam(userId, pagination, publicScope);
  }
}
