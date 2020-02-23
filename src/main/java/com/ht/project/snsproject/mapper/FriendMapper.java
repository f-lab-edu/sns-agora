package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.friend.FriendStatusInsert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendMapper {

    void requestFriend(String userId, String targetId);

    void deleteFriendRequest(String userId, String targetId);

    void updateFriendRequestToFriend(FriendStatusInsert friendStatusInsert);

    Friend getFriendStatus(String userId, String targetId);
}
