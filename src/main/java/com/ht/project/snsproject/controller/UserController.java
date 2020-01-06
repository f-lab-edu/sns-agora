package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.model.UserJoin;
import com.ht.project.snsproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<Result> join(@RequestBody UserJoin userJoin){
        userService.join(userJoin);
        return new ResponseEntity<>(Result.SUCCESS, HttpStatus.CREATED);
    }

    @GetMapping
    public  ResponseEntity<Result> userIdCheck(@RequestParam String userId){
        if(userService.userIdCheck(userId)==0){
            return new ResponseEntity<>(Result.SUCCESS,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Result.CONFLICT,HttpStatus.CONFLICT);
        }
    }
}

enum Result {
    SUCCESS("SUCCESS"),
    CONFLICT("CONFLICT");

    private final String text;

    Result(final String text){
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
