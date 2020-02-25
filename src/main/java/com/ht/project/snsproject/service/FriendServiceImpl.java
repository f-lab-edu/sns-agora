package com.ht.project.snsproject.service;

import com.ht.project.snsproject.Exception.DuplicateRequestException;
import com.ht.project.snsproject.Exception.InvalidApproachException;
import com.ht.project.snsproject.enumeration.AlarmType;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.mapper.FriendMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.friend.FriendList;
import com.ht.project.snsproject.model.friend.FriendListParam;
import com.ht.project.snsproject.model.friend.FriendStatusInsert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    FriendMapper friendMapper;

    @Autowired
    AlarmService alarmService;

    @Override
    public void requestFriend(String userId, String targetId) {

        FriendStatus status = friendMapper.getFriendRelationStatus(targetId, userId).getFriendStatus();

        switch (status){
            case NONE:
                friendMapper.requestFriend(userId, targetId);
                alarmService.insertAlarm(userId, targetId, AlarmType.FRIEND_REQ);
                break;

            case BLOCK:
                throw new InvalidApproachException("유효하지 않은 접근입니다.");

            default:
                throw new DuplicateRequestException("중복된 요청입니다.");
        }

    }

    @Override
    public void deleteFriendRequest(String userId, String targetId) {

        FriendStatus status = friendMapper.getFriendRelationStatus(userId, targetId).getFriendStatus();

        switch(status){
            case REQUEST:
                friendMapper.deleteFriend(userId, targetId);
                alarmService.deleteAlarm(userId, targetId, AlarmType.FRIEND_REQ);
                break;

            case RECEIVE:
                friendMapper.deleteFriend(userId, targetId);
                break;

            default:
                throw new InvalidApproachException("유효하지 않은 접근입니다.");
        }
    }

    @Override
    public void permitFriendRequest(String userId, String targetId) {

        if(friendMapper.getFriendRelationStatus(userId, targetId).getFriendStatus()!=FriendStatus.RECEIVE){
            throw new InvalidApproachException("유효하지 않은 접근입니다.");
        }

        FriendStatusInsert friendStatusInsert = FriendStatusInsert.create(userId, targetId, FriendStatus.FRIEND);

        friendMapper.updateFriendRequestToFriend(friendStatusInsert);
        alarmService.insertAlarm(userId, targetId, AlarmType.FRIEND_RES);
    }

    @Override
    public void cancelFriend(String userId, String targetId) {

        if(friendMapper.getFriendRelationStatus(userId, targetId).getFriendStatus()!=FriendStatus.FRIEND){
            throw new InvalidApproachException("유효하지 않은 접근입니다.");
        }
        friendMapper.deleteFriend(userId, targetId);
    }

    @Override
    public List<FriendList> getFriendRequests(String userId, Pagination pagination) {

        return friendMapper.getFriendList(FriendListParam.create(userId, pagination, FriendStatus.RECEIVE));
    }

    @Override
    public List<FriendList> getFriendList(String userId, Pagination pagination) {

        return friendMapper.getFriendList(FriendListParam.create(userId,pagination,FriendStatus.FRIEND));
    }

    @Override
    public void blockUser(String userId, String targetId) {
        friendMapper.deleteFriend(userId,targetId);
        friendMapper.blockUser(userId, targetId);
    }

    @Override
    public void unblockUser(String userId, String targetId) {
        friendMapper.deleteBlockUser(userId, targetId);
    }

    @Override
    public List<FriendList> getBlockUserList(String userId, Pagination pagination) {
        return friendMapper.getFriendList(FriendListParam.create(userId, pagination, FriendStatus.BLOCK));
    }

    @Override
    public Friend getFriendRelationStatus(String userId, String targetId) {

        return friendMapper.getFriendRelationStatus(userId, targetId);
    }
}
