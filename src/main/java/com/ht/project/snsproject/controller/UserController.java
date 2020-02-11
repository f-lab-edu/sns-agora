package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginMethodCheck;
import com.ht.project.snsproject.model.user.*;
import com.ht.project.snsproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public HttpStatus joinUser(@RequestBody @Valid UserJoinRequest userJoinRequest){
        userService.joinUser(userJoinRequest);
        return HttpStatus.CREATED;
    }

    @GetMapping
    public HttpStatus isDuplicateUserId(@RequestParam String userId){
        if(!userService.isDuplicateUserId(userId)){
            return HttpStatus.OK;
        } else {
            return HttpStatus.CONFLICT;
        }
    }

    @LoginMethodCheck
    @PutMapping("/account")
    public HttpStatus updateUserProfile(@RequestBody UserProfileParam userProfileParam, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        UserProfile userProfile = new UserProfile(userInfo.getId(),
                userProfileParam.getNickname(),
                userProfileParam.getEmail(),
                userProfileParam.getBirth());
        userService.updateUserProfile(userProfile);
        return HttpStatus.OK;
    }


    @PostMapping("/login")
    public HttpStatus login(@RequestBody @Valid UserLogin userLogin, HttpSession httpSession) {

        if(!userService.existUser(userLogin, httpSession)){
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }

    @LoginMethodCheck
    @PostMapping("/logout")
    public HttpStatus logout(HttpSession httpSession) {
        httpSession.invalidate();
        return HttpStatus.NO_CONTENT;
    }

    @LoginMethodCheck
    @DeleteMapping("/account")
    public HttpStatus deleteUser(@RequestBody UserPasswordVerify userPasswordVerify, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        if(!userService.verifyPassword(userInfo.getUserId(), userPasswordVerify.getPassword())){
            return HttpStatus.BAD_REQUEST;
        }
        userService.deleteUser(userInfo.getUserId());
        httpSession.invalidate();
        return HttpStatus.NO_CONTENT;
    }

    @LoginMethodCheck
    @PutMapping("/account/password")
    public HttpStatus updateUserPassword(@RequestBody @Valid UserPassword userPassword, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        userService.updateUserPassword(userInfo.getUserId(),userPassword);
        return HttpStatus.OK;
    }

}