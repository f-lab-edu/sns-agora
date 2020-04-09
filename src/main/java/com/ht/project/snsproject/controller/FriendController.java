package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.friend.FriendList;
import com.ht.project.snsproject.service.FriendService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friends")
public class FriendController {

  @Autowired
  FriendService friendService;

  @LoginCheck
  @PostMapping("/{targetId}/requests")
  public HttpStatus requestFriend(@PathVariable String targetId, String userId) {

    friendService.requestFriend(userId, targetId);

    return HttpStatus.CREATED;
  }

  @LoginCheck
  @DeleteMapping("/{targetId}/requests")
  public HttpStatus deleteFriendRequest(@PathVariable String targetId, String userId) {

    friendService.deleteFriendRequest(userId, targetId);

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @DeleteMapping("/{targetId}/requests/deny")
  public HttpStatus denyFriendRequest(@PathVariable String targetId, String userId) {

    friendService.denyFriendRequest(userId, targetId);

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @PutMapping("/{targetId}/requests")
  public HttpStatus permitFriendRequest(@PathVariable String targetId, String userId) {

    friendService.permitFriendRequest(userId, targetId);

    return HttpStatus.OK;
  }

  @LoginCheck
  @GetMapping("/requests")
  public ResponseEntity<List<FriendList>> getFriendRequests(
          @RequestParam(required = false) Integer cursor, String userId) {

    return ResponseEntity.ok(friendService.getFriendRequests(userId,Pagination.pageInfo(cursor)));
  }

  @LoginCheck
  @GetMapping
  public ResponseEntity<List<FriendList>> getFriendList(
          @RequestParam(required = false) Integer cursor, String userId) {

    return ResponseEntity.ok(friendService.getFriendList(userId, Pagination.pageInfo(cursor)));
  }

  @LoginCheck
  @GetMapping("/{targetId}")
  public ResponseEntity<Friend> getFriendRelationStatus(@PathVariable String targetId,
                                                        String userId) {

    return ResponseEntity.ok(friendService.getFriendRelationStatus(userId, targetId));
  }

  @LoginCheck
  @PostMapping("/{targetId}/blocks")
  public HttpStatus blockUser(@PathVariable String targetId, String userId) {

    friendService.blockUser(userId, targetId);

    return HttpStatus.OK;
  }

  @LoginCheck
  @DeleteMapping("/{targetId}/blocks")
  public HttpStatus unblockUser(@PathVariable String targetId, String userId) {

    friendService.unblockUser(userId,targetId);

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @GetMapping("/blocks")
  public ResponseEntity<List<FriendList>> getBlockUserList(
          @RequestParam(required = false) Integer cursor, String userId) {

    return ResponseEntity.ok(friendService.getBlockUserList(userId, Pagination.pageInfo(cursor)));
  }

  @LoginCheck
  @DeleteMapping("/{targetId}")
  public HttpStatus cancelFriend(@PathVariable String targetId, String userId) {

    friendService.cancelFriend(userId, targetId);

    return HttpStatus.NO_CONTENT;
  }
}
