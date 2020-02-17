package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginMethodCheck;
import com.ht.project.snsproject.enumeration.FriendStatus;
import com.ht.project.snsproject.enumeration.Type;
import com.ht.project.snsproject.model.friend.Friend;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.service.AlarmService;

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

    @Autowired
    AlarmService alarmService;

    @LoginMethodCheck
    @PostMapping("/{targetId}")
    public HttpStatus requestFriend(@PathVariable String targetId, HttpSession httpSession) {
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        String url = "/users/" + targetId;
        friendService.requestFriend(userId, targetId, FriendStatus.REQUEST);
        alarmService.insertAlarm(userId, targetId, Type.FRIEND_REQ, url);
        return HttpStatus.CREATED;
    }

    @LoginMethodCheck
    @DeleteMapping("/{targetId}")
    public HttpStatus deleteFriendRequest(@PathVariable String targetId, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        String userId = userInfo.getUserId();
        alarmService.deleteAlarm(userId, targetId, Type.FRIEND_REQ);
        friendService.deleteFriendRequest(userId, targetId);
        return HttpStatus.NO_CONTENT;
    }

    @LoginMethodCheck
    @GetMapping("/{targetId}")
    public ResponseEntity<Friend> getFriendStatus(@PathVariable String targetId, HttpSession httpSession) {
        User userInfo = (User) httpSession.getAttribute("userInfo");
        return new ResponseEntity<>(friendService.getFriendStatus(userInfo.getUserId(),targetId),HttpStatus.OK);
    }
}
