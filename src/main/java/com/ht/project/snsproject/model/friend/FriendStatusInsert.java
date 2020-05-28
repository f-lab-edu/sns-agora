package com.ht.project.snsproject.model.friend;

import com.ht.project.snsproject.enumeration.FriendStatus;
import lombok.Value;

@Value
public class FriendStatusInsert {

  String userId;

  String targetId;

  FriendStatus friendStatus;

  public static FriendStatusInsert create(String userId,
                                          String targetId, FriendStatus friendStatus) {
    return new FriendStatusInsert(userId, targetId, friendStatus);
  }
}
