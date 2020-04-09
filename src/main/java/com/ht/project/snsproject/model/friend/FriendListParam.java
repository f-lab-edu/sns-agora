package com.ht.project.snsproject.model.friend;

import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.model.Pagination;
import lombok.Value;

@Value
public class FriendListParam {

    String userId;

    Pagination pagination;

    FriendStatus friendStatus;

    public static FriendListParam create(String userId, Pagination pagination, FriendStatus friendStatus){
        return new FriendListParam(userId, pagination, friendStatus);
    }
}
