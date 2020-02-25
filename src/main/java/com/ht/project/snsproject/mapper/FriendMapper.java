package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.friend.FriendList;
import com.ht.project.snsproject.model.friend.FriendListParam;
import com.ht.project.snsproject.model.friend.FriendStatusInsert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendMapper {

    void requestFriend(String userId, String targetId);

    void deleteFriend(String userId, String targetId);

    void updateFriendRequestToFriend(FriendStatusInsert friendStatusInsert);

    List<FriendList> getFriendList(FriendListParam friendListParam);

    void blockUser(String userId, String targetId);

    void deleteBlockUser(String userId, String targetId);

    Friend getFriendRelationStatus(String userId, String targetId);
}
