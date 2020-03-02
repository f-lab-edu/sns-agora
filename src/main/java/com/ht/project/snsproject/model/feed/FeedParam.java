package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.enumeration.FriendStatus;
import lombok.Value;

@Value
public class FeedParam {

    int id;

    String userId;

    FriendStatus friendStatus;

    public static FeedParam create(int id, String userId, FriendStatus friendStatus){

        return new FeedParam(id, userId, friendStatus);
    }
}
