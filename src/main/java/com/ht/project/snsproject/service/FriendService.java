package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.friend.FriendStatusInsert;

public interface FriendService {

    void requestFriend (String userId, String targetId);

    void deleteFriendRequest(String userId, String targetId);

    void permitFriendRequest(String userId, String targetId);

    Friend getFriendStatus(String userId, String targetId);
}
