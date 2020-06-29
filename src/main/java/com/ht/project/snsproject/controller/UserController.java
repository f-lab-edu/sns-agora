package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.annotation.UserInfo;
import com.ht.project.snsproject.model.user.*;
import com.ht.project.snsproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  UserService userService;

  @PostMapping
  public HttpStatus joinUser(@RequestBody @Valid UserJoinRequest userJoinRequest) {

    userService.joinUser(userJoinRequest);

    return HttpStatus.CREATED;
  }

  @GetMapping
  public HttpStatus isDuplicateUserId(@RequestParam String userId) {

    if (!userService.isDuplicateUserId(userId)) {

      return HttpStatus.OK;
    } else {

      return HttpStatus.CONFLICT;
    }
  }

  @LoginCheck
  @PutMapping("/account")
  public HttpStatus updateUserProfile(@RequestParam("file") MultipartFile file,
                                      UserProfileParam userProfileParam,
                                      @UserInfo User user) {


    userService.updateUserProfile(userProfileParam, user.getUserId(), file);

    return HttpStatus.OK;
  }


  @PostMapping("/login")
  public HttpStatus login(@RequestBody @Valid UserLogin userLogin, HttpSession httpSession) {

    if (!userService.existUser(userLogin, httpSession)) {

      return HttpStatus.BAD_REQUEST;
    }

    return HttpStatus.OK;
  }

  @LoginCheck
  @PostMapping("/logout")
  public HttpStatus logout(HttpSession httpSession) {

    userService.logout(httpSession);
    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @DeleteMapping("/account")
  public HttpStatus deleteUser(@RequestBody String password,
                               @UserInfo User user, HttpSession httpSession) {
    String userId = user.getUserId();
    if (!userService.verifyPassword(userId, password)) {

      return HttpStatus.BAD_REQUEST;
    }
    userService.deleteUser(httpSession);

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @PutMapping("/account/password")
  public HttpStatus updateUserPassword(@RequestBody @Valid UserPassword userPassword,
                                       @UserInfo User user) {

    userService.updateUserPassword(user.getUserId(), userPassword);

    return HttpStatus.OK;
  }

  @LoginCheck
  @GetMapping("/{targetId}")
  public ResponseEntity<UserProfile> getUserProfile(@PathVariable String targetId) {

    return ResponseEntity.ok(userService.getUserProfile(targetId));
  }
}