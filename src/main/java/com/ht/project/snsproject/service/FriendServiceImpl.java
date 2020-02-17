package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.mapper.FriendMapper;
import com.ht.project.snsproject.model.friend.Friend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    FriendMapper friendMapper;

    @Override
    public void requestFriend(String userId, String targetId, FriendStatus friendStatus) {
        friendMapper.requestFriend(userId, targetId, friendStatus);
    }

    @Override
    public void deleteFriendRequest(String userId, String targetId) {
        friendMapper.deleteFriendRequest(userId, targetId);
    }

    @Override
    public Friend getFriendStatus(String userId, String targetId) {
        return friendMapper.getFriendStatus(userId, targetId);
    }
}
