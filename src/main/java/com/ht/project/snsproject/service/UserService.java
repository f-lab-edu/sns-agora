package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.user.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

public interface UserService {

  void joinUser(UserJoinRequest userJoinRequest);

  boolean isDuplicateUserId(String userId);

  void updateUserProfile(UserProfileParam userProfileParam,String userId, MultipartFile profile);

  void exists(UserLogin userLogin, HttpSession httpSession);

  User findUserByUserId(String userId);

  void logout(String userId, HttpSession httpSession);

  void deleteUser(String userId, String password, HttpSession httpSession);

  void updateUserPassword(String userId, UserPassword userPassword);
}
