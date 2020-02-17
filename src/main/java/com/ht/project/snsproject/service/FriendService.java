package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.model.friend.Friend;

public interface FriendService {

    void requestFriend (String userId, String targetId, FriendStatus friendStatus);

    void deleteFriendRequest(String userId, String targetId);

    Friend getFriendStatus(String userId, String targetId);
}
