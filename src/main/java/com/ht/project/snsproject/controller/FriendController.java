package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.Pagination;
import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.friend.FriendList;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    FriendService friendService;

    @LoginCheck
    @PostMapping("/{targetId}/requests")
    public HttpStatus requestFriend(@PathVariable String targetId, HttpSession httpSession) {
        User userInfo = (User) httpSession.getAttribute("userInfo");
        friendService.requestFriend(userInfo.getUserId(), targetId);
        return HttpStatus.CREATED;
    }

    @LoginCheck
    @DeleteMapping("/{targetId}/requests")
    public HttpStatus deleteFriendRequest(@PathVariable String targetId, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        friendService.deleteFriendRequest(userId, targetId);
        return HttpStatus.NO_CONTENT;
    }

    @LoginCheck
    @PutMapping("/{targetId}/requests")
    public HttpStatus permitFriendRequest(@PathVariable String targetId, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        friendService.permitFriendRequest(userId, targetId);
        return HttpStatus.OK;
    }

    @LoginCheck
    @GetMapping("/requests")
    public ResponseEntity<List<FriendList>> getFriendRequests(@RequestParam(required = false) Integer cursor,
                                                              HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        return ResponseEntity.ok(friendService.getFriendRequests(userId,Pagination.pageInfo(cursor)));
    }

    @LoginCheck
    @GetMapping
    public ResponseEntity<List<FriendList>> getFriendList(@RequestParam(required = false) Integer cursor,
                                                          HttpSession httpSession){
        User userInfo =(User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        return ResponseEntity.ok(friendService.getFriendList(userId, Pagination.pageInfo(cursor)));
    }

    @LoginCheck
    @GetMapping("/{targetId}")
    public ResponseEntity<Friend> getFriendRelationStatus(@PathVariable String targetId, HttpSession httpSession) {
        User userInfo = (User) httpSession.getAttribute("userInfo");
        return ResponseEntity.ok(friendService.getFriendRelationStatus(userInfo.getUserId(),targetId));
    }

    @LoginCheck
    @PostMapping("/{targetId}/blocks")
    public HttpStatus blockUser(@PathVariable String targetId, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        friendService.blockUser(userInfo.getUserId(), targetId);
        return HttpStatus.OK;
    }

    @LoginCheck
    @DeleteMapping("/{targetId}/blocks")
    public HttpStatus unblockUser(@PathVariable String targetId, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        friendService.unblockUser(userInfo.getUserId(),targetId);
        return HttpStatus.NO_CONTENT;
    }

    @LoginCheck
    @GetMapping("/blocks")
    public ResponseEntity<List<FriendList>> getBlockUserList (@RequestParam(required = false) Integer cursor,
                                                              HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        return ResponseEntity.ok(friendService.getBlockUserList(userId, Pagination.pageInfo(cursor)));
    }

    @LoginCheck
    @DeleteMapping("/{targetId}")
    public HttpStatus cancelFriend(@PathVariable String targetId, HttpSession httpSession){
        User userInfo = (User)  httpSession.getAttribute("userInfo");
        friendService.cancelFriend(userInfo.getUserId(),targetId);
        return HttpStatus.NO_CONTENT;
    }
}
