package com.ht.project.snsproject.service;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.*;

import javax.servlet.http.HttpSession;

public interface UserService {

    void joinUser(UserJoin userJoin);

    boolean checkDuplicateUserId(String userId);

    void updateUserProfile(UserProfile userProfile);

    boolean getUser(UserLogin userLogin, HttpSession httpSession);

    boolean verifyPassword(String userId, String password);

    void deleteUser(String userId);

    void updateUserPassword(String userId, UserPassword userPassword);
}
