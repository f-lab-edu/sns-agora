package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.friend.FriendList;
import java.util.List;

public interface FriendService {

  void requestFriend(String userId, String targetId);

  void deleteFriendRequest(String userId, String targetId);

  void denyFriendRequest(String userId, String targetId);

  void permitFriendRequest(String userId, String targetId);

  void cancelFriend(String userId, String targetId);

  List<FriendList> getFriendRequests(String userId, Pagination pagination);

  List<FriendList> getFriendList(String userId, Pagination pagination);

  void blockUser(String userId, String targetId);

  void unblockUser(String userId, String targetId);

  List<FriendList> getBlockUserList(String userId, Pagination pagination);

  Friend getFriendRelationStatus(String userId, String targetId);
}
