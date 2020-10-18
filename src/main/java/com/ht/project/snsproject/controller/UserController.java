package com.ht.project.snsproject.controller;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.model.user.UserJoinRequest;
import com.ht.project.snsproject.model.user.UserLogin;
import com.ht.project.snsproject.model.user.UserPassword;
import com.ht.project.snsproject.model.user.UserPasswordVerify;
import com.ht.project.snsproject.model.user.UserProfile;
import com.ht.project.snsproject.model.user.UserProfileParam;
import com.ht.project.snsproject.service.UserService;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;




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
  public HttpStatus updateUserProfile(@RequestBody UserProfileParam userProfileParam,
                                      HttpSession httpSession) {

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

    if (!userService.existUser(userLogin, httpSession)) {

      return HttpStatus.BAD_REQUEST;
    }

    return HttpStatus.OK;
  }

  @LoginCheck
  @PostMapping("/logout")
  public HttpStatus logout(HttpSession httpSession) {

    User userInfo = (User) httpSession.getAttribute("userInfo");
    httpSession.invalidate();

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @DeleteMapping("/account")
  public HttpStatus deleteUser(@RequestBody UserPasswordVerify userPasswordVerify,
                               HttpSession httpSession) {

    User userInfo = (User) httpSession.getAttribute("userInfo");
    if (!userService.verifyPassword(userInfo.getUserId(), userPasswordVerify.getPassword())) {

      return HttpStatus.BAD_REQUEST;
    }
    userService.deleteUser(userInfo.getUserId());
    httpSession.invalidate();

    return HttpStatus.NO_CONTENT;
  }

  @LoginCheck
  @PutMapping("/account/password")
  public HttpStatus updateUserPassword(@RequestBody @Valid UserPassword userPassword,
                                       HttpSession httpSession) {

    User userInfo = (User) httpSession.getAttribute("userInfo");
    userService.updateUserPassword(userInfo.getUserId(),userPassword);

    return HttpStatus.OK;
  }

  @LoginCheck
  @GetMapping("/{targetId}")
  public ResponseEntity<UserProfile> getUserProfile(@PathVariable String targetId) {

    return ResponseEntity.ok(userService.getUserProfile(targetId));
  }
}