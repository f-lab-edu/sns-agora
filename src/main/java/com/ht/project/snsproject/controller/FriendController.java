package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.friend.FriendList;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

  private final FriendService friendService;

  @LoginCheck
  @PostMapping("/{targetId}/requests")
  public HttpStatus requestFriend(@PathVariable String targetId, @UserInfo User user) {

    friendService.requestFriend(user.getUserId(), targetId);

    return HttpStatus.CREATED;
  }

  @LoginCheck
  @DeleteMapping("/{targetId}/requests")
  public HttpStatus deleteFriendRequest(@PathVariable String targetId, @UserInfo User user) {

    friendService.deleteFriendRequest(user.getUserId(), targetId);

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @DeleteMapping("/{targetId}/requests/deny")
  public HttpStatus denyFriendRequest(@PathVariable String targetId, @UserInfo User user) {

    friendService.denyFriendRequest(user.getUserId(), targetId);

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @PutMapping("/{targetId}/requests")
  public HttpStatus permitFriendRequest(@PathVariable String targetId, @UserInfo User user) {

    friendService.permitFriendRequest(user.getUserId(), targetId);

    return HttpStatus.OK;
  }

  @LoginCheck
  @GetMapping("/requests")
  public ResponseEntity<List<FriendList>> getFriendRequests(
          @RequestParam(required = false) Integer cursor, @UserInfo User user) {

    return ResponseEntity.ok(friendService.getFriendRequests(user.getUserId(),Pagination.pageInfo(cursor)));
  }

  @LoginCheck
  @GetMapping
  public ResponseEntity<List<FriendList>> getFriendList(
          @RequestParam(required = false) Integer cursor, @UserInfo User user) {

    return ResponseEntity.ok(friendService.getFriendList(user.getUserId(), Pagination.pageInfo(cursor)));
  }

  @LoginCheck
  @GetMapping("/{targetId}")
  public ResponseEntity<FriendStatus> getFriendRelationStatus(@PathVariable String targetId,
                                                              @UserInfo User user) {

    return ResponseEntity.ok(friendService.getFriendStatus(user.getUserId(), targetId));
  }

  @LoginCheck
  @PostMapping("/{targetId}/blocks")
  public HttpStatus blockUser(@PathVariable String targetId, @UserInfo User user) {

    friendService.blockUser(user.getUserId(), targetId);

    return HttpStatus.OK;
  }

  @LoginCheck
  @DeleteMapping("/{targetId}/blocks")
  public HttpStatus unblockUser(@PathVariable String targetId,
                                @UserInfo User user) {

    friendService.unblockUser(user.getUserId(),targetId);

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @GetMapping("/blocks")
  public ResponseEntity<List<FriendList>> getBlockUserList(
          @RequestParam(required = false) Integer cursor, @UserInfo User user) {

    return ResponseEntity.ok(friendService.getBlockUserList(user.getUserId(), Pagination.pageInfo(cursor)));
  }

  @LoginCheck
  @DeleteMapping("/{targetId}")
  public HttpStatus cancelFriend(@PathVariable String targetId,
                                 @UserInfo User user) {

    friendService.cancelFriend(user.getUserId(), targetId);

    return HttpStatus.NO_CONTENT;
  }
}
