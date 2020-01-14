package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.model.UserJoin;
import com.ht.project.snsproject.model.UserProfile;
import com.ht.project.snsproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public HttpStatus joinUser(@RequestBody UserJoin userJoin){
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

    @PutMapping("/{id}")
    public HttpStatus updateUserProfile(@RequestBody UserProfile userProfile){
        userService.updateUserProfile(userProfile);
        return HttpStatus.OK;
    }

}