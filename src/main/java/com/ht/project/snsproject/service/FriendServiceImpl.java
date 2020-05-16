package com.ht.project.snsproject.service;

import com.ht.project.snsproject.enumeration.AlarmType;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.PublicScope;
import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.exception.InvalidApproachException;
import com.ht.project.snsproject.mapper.FriendMapper;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.friend.FriendList;
import com.ht.project.snsproject.model.friend.FriendListParam;
import com.ht.project.snsproject.model.friend.FriendStatusInsert;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendServiceImpl implements FriendService {

  @Autowired
  FriendMapper friendMapper;

  @Autowired
  AlarmService alarmService;

  @Transactional
  @Override
  public void requestFriend(String userId, String targetId) {

    FriendStatus status = friendMapper.getFriendRelationStatus(targetId, userId).getFriendStatus();

    switch (status) {
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
  public FriendStatus getFriendStatus(String userId, String targetId) {

    if(userId.equals(targetId)) {
      return FriendStatus.ME;
    }

    return friendMapper.getFriendRelationStatus(userId, targetId)
            .getFriendStatus();
  }

  @Transactional
  @Override
  public void deleteFriendRequest(String userId, String targetId) {

    FriendStatus status = friendMapper.getFriendRelationStatus(userId, targetId).getFriendStatus();

    if (status != FriendStatus.REQUEST) {
      throw new InvalidApproachException("유효하지 않은 접근입니다.");
    }

    friendMapper.deleteFriend(userId, targetId);
    alarmService.deleteRequestAlarm(userId, targetId, AlarmType.FRIEND_REQ);
  }

  @Transactional
  @Override
  public void denyFriendRequest(String userId, String targetId) {

    FriendStatus status = friendMapper.getFriendRelationStatus(userId, targetId).getFriendStatus();

    if (status != FriendStatus.RECEIVE) {
      throw new InvalidApproachException("유효하지 않은 접근입니다.");
    }

    friendMapper.deleteFriend(userId, targetId);
  }

  @Transactional
  @Override
  public void permitFriendRequest(String userId, String targetId) {

    if (friendMapper.getFriendRelationStatus(userId, targetId)
            .getFriendStatus() != FriendStatus.RECEIVE) {
      throw new InvalidApproachException("유효하지 않은 접근입니다.");
    }

    FriendStatusInsert friendStatusInsert = FriendStatusInsert
            .create(userId, targetId, FriendStatus.FRIEND);

    friendMapper.updateFriendRequestToFriend(friendStatusInsert);
    alarmService.insertAlarm(userId, targetId, AlarmType.FRIEND_RES);
  }

  @Transactional
  @Override
  public void cancelFriend(String userId, String targetId) {

    if (friendMapper.getFriendRelationStatus(userId, targetId)
            .getFriendStatus() != FriendStatus.FRIEND) {
      throw new InvalidApproachException("유효하지 않은 접근입니다.");
    }
    friendMapper.deleteFriend(userId, targetId);
  }

  @Override
  public List<FriendList> getFriendRequests(String userId, Pagination pagination) {

    return friendMapper.getFriendList(
          FriendListParam.create(userId, pagination, FriendStatus.RECEIVE));
  }

  @Override
  public List<FriendList> getFriendList(String userId, Pagination pagination) {

    return friendMapper.getFriendList(
            FriendListParam.create(userId,pagination,FriendStatus.FRIEND));
  }

  @Transactional
  @Override
  public void blockUser(String userId, String targetId) {

    FriendStatus friendStatus = friendMapper.getFriendRelationStatus(userId, targetId)
            .getFriendStatus();

    switch (friendStatus) {
      case NONE:
        friendMapper.blockUser(userId, targetId);
        break;
      case BLOCK:
        throw new DuplicateRequestException("중복된 요청입니다.");
      case ME:
        throw new InvalidApproachException("유효하지 않은 접근입니다.");
      default:
        friendMapper.deleteFriend(userId,targetId);
        friendMapper.blockUser(userId, targetId);
    }
  }

  @Transactional
  @Override
  public void unblockUser(String userId, String targetId) {

    FriendStatus friendStatus = friendMapper.getFriendRelationStatus(userId, targetId)
            .getFriendStatus();

    if (friendStatus != FriendStatus.BLOCK) {
      throw new InvalidApproachException("유효하지 않은 접근입니다.");
    }

    friendMapper.deleteBlockUser(userId, targetId);
  }

  @Override
  public List<FriendList> getBlockUserList(String userId, Pagination pagination) {

    return friendMapper.getFriendList(
            FriendListParam.create(userId, pagination, FriendStatus.BLOCK));
  }

  @Override
  public Friend getFriendRelationStatus(String userId, String targetId) {

    return friendMapper.getFriendRelationStatus(userId, targetId);
  }

  @Override
  public boolean isFeedReadableByFriendStatus(PublicScope publicScope, FriendStatus friendStatus) {

    boolean isReadable = false;

    switch (friendStatus) {

      case ME:
        if(publicScope == PublicScope.ALL ||
                publicScope == PublicScope.FRIENDS ||
                publicScope == PublicScope.ME) {
          isReadable = true;
        }
        break;

      case FRIEND:
        if(publicScope == PublicScope.ALL ||
                publicScope == PublicScope.FRIENDS) {
          isReadable =true;
        }
        break;

      case BLOCK:
        break;

      default:
        if(publicScope == PublicScope.ALL) {
          isReadable = true;
        }
    }

    return isReadable;
  }
}
