package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.user.*;

import javax.servlet.http.HttpSession;

public interface UserService {

  void joinUser(UserJoinRequest userJoinRequest);

  boolean isDuplicateUserId(String userId);

  void updateUserProfile(UserProfile userProfile);

  boolean existUser(UserLogin userLogin, HttpSession httpSession);

  User getUserInfoCache(String userId);

  void logout(HttpSession httpSession);

  boolean verifyPassword(String userId, String password);

  void deleteUser(HttpSession httpSession);

  void updateUserPassword(String userId, UserPassword userPassword);

  UserProfile getUserProfile(String targetId);
}
