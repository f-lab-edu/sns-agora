package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.model.Pagination;
import lombok.Value;

@Value
public class FriendsFeedIdParam {

  String userId;

  Pagination pagination;
}
