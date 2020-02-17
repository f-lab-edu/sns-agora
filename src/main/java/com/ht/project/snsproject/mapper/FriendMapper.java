package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.model.friend.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FriendMapper {

    void requestFriend(@Param("userId") String userId, @Param("targetId") String targetId,
                       @Param("friendStatus") FriendStatus friendStatus);

    void deleteFriendRequest(String userId, String targetId);

    Friend getFriendStatus(String userId, String targetId);
}
