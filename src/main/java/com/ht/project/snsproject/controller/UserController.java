package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.*;
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
    public HttpStatus joinUser(@RequestBody @Valid UserJoin userJoin){
        userService.joinUser(userJoin);
        return HttpStatus.CREATED;
    }

    @GetMapping
    public HttpStatus checkDuplicateUserId(@RequestParam String userId){
        if(!userService.checkDuplicateUserId(userId)){
            return HttpStatus.OK;
        } else {
            return HttpStatus.CONFLICT;
        }
    }

    @LoginCheck
    @PutMapping("/{id}")
    public HttpStatus updateUserProfile(@PathVariable("id") int id, @RequestBody UserProfileParam userProfileParam){
        UserProfile userProfile = new UserProfile(id, userProfileParam.getNickname(), userProfileParam.getEmail(), userProfileParam.getBirth());
        userService.updateUserProfile(userProfile);
        return HttpStatus.OK;
    }


    @PostMapping("/login")
    public HttpStatus login(@RequestBody @Valid UserLogin userLogin, HttpSession httpSession) {

        if(!userService.getUser(userLogin, httpSession)){
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }

    @LoginCheck
    @PostMapping("/logout")
    public HttpStatus logout(HttpSession httpSession) {
        httpSession.invalidate();
        return HttpStatus.NO_CONTENT;
    }

    @LoginCheck
    @DeleteMapping("/{id}")
    public HttpStatus deleteUser(@RequestBody UserPasswordVerify password, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        if(!userService.verifyPassword(userInfo.getUserId(), password.getPassword())){
            return HttpStatus.BAD_REQUEST;
        }
        userService.deleteUser(userInfo.getUserId());
        httpSession.invalidate();
        return HttpStatus.NO_CONTENT;
    }

    @LoginCheck
    @PutMapping("/{id}/password")
    public HttpStatus updateUserPassword(@RequestBody @Valid UserPassword userPassword, HttpSession httpSession){
        User userInfo = (User) httpSession.getAttribute("userInfo");
        userService.updateUserPassword(userInfo.getUserId(),userPassword);
        return HttpStatus.OK;
    }


}