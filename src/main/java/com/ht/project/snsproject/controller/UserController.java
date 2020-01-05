package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.model.UserJoin;
import com.ht.project.snsproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<String> join(@RequestBody UserJoin userJoin){
        userService.join(userJoin);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }
}
