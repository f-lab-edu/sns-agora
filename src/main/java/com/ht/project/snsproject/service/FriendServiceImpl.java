package com.ht.project.snsproject.service;

import com.ht.project.snsproject.Exception.DuplicateRequestException;
import com.ht.project.snsproject.Exception.InvalidApproachException;
import com.ht.project.snsproject.enumeration.AlarmType;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.mapper.FriendMapper;
import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.friend.FriendStatusInsert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    FriendMapper friendMapper;

    @Autowired
    AlarmService alarmService;

    @Override
    public void requestFriend(String userId, String targetId) {
        if(friendMapper.getFriendStatus(userId, targetId).getFriendStatus()!=FriendStatus.NONE){
            throw new DuplicateRequestException("중복된 요청입니다.");
        }
        friendMapper.requestFriend(userId, targetId);
        alarmService.insertAlarm(userId, targetId, AlarmType.FRIEND_REQ);
    }

    @Override
    public void deleteFriendRequest(String userId, String targetId) {
        friendMapper.deleteFriendRequest(userId, targetId);
        alarmService.deleteAlarm(userId, targetId, AlarmType.FRIEND_REQ);
    }

    @Override
    public void permitFriendRequest(String userId, String targetId) {
        if(friendMapper.getFriendStatus(userId, targetId).getFriendStatus()!=FriendStatus.RECEIVE){
            throw new InvalidApproachException("유효하지 않은 접근입니다.");
        }

        FriendStatusInsert friendStatusInsert = FriendStatusInsert.create(userId, targetId, FriendStatus.FRIEND);

        friendMapper.updateFriendRequestToFriend(friendStatusInsert);
        alarmService.insertAlarm(userId, targetId, AlarmType.FRIEND_RES);
    }


    @Override
    public Friend getFriendStatus(String userId, String targetId) {
        return friendMapper.getFriendStatus(userId, targetId);
    }
}
