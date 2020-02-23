package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.friend.FriendStatusInsert;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    FriendService friendService;

    @LoginCheck
    @PostMapping("/{targetId}")
    public HttpStatus requestFriend(@PathVariable String targetId, HttpSession httpSession) {
        User userInfo = (User) httpSession.getAttribute("userInfo");
        friendService.requestFriend(userInfo.getUserId(), targetId);
        return HttpStatus.CREATED;
    }

    @LoginCheck
    @DeleteMapping("/{targetId}")
    public HttpStatus deleteFriendRequest(@PathVariable String targetId, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        friendService.deleteFriendRequest(userId, targetId);
        return HttpStatus.NO_CONTENT;
    }

    @PutMapping("/{targetId}")
    public HttpStatus permitFriendRequest(@PathVariable String targetId, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        friendService.permitFriendRequest(userId, targetId);
        return HttpStatus.OK;
    }

    @LoginCheck
    @GetMapping("/{targetId}")
    public ResponseEntity<Friend> getFriendStatus(@PathVariable String targetId, HttpSession httpSession) {
        User userInfo = (User) httpSession.getAttribute("userInfo");
        return ResponseEntity.ok(friendService.getFriendStatus(userInfo.getUserId(),targetId));
    }
}
