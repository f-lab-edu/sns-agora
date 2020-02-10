package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.user.UserJoinRequest;
import com.ht.project.snsproject.model.user.UserLogin;
import com.ht.project.snsproject.model.user.UserPassword;
import com.ht.project.snsproject.model.user.UserProfile;

import javax.servlet.http.HttpSession;

public interface UserService {

    void joinUser(UserJoinRequest userJoinRequest);

    boolean isDuplicateUserId(String userId);

    void updateUserProfile(UserProfile userProfile);

    boolean existUser(UserLogin userLogin, HttpSession httpSession);

    boolean verifyPassword(String userId, String password);

    void deleteUser(String userId);

    void updateUserPassword(String userId, UserPassword userPassword);
}
