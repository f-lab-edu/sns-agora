package com.ht.project.snsproject.model.feed;

import com.ht.project.snsproject.model.Pagination;
import lombok.Value;

@Value
public class FriendsFeedList {

    String userId;

    Pagination pagination;

    public static FriendsFeedList create(String userId, Pagination pagination){
        return new FriendsFeedList(userId, pagination);
    }
}
