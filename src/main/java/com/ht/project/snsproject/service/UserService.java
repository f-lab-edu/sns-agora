package com.ht.project.snsproject.service;

import com.ht.project.snsproject.annotation.LoginCheck;
import com.ht.project.snsproject.model.User;
import com.ht.project.snsproject.model.UserJoin;
import com.ht.project.snsproject.model.UserLogin;
import com.ht.project.snsproject.model.UserProfile;

public interface UserService {

    void joinUser(UserJoin userJoin);

    boolean checkDuplicateUserId(String userId);

    void updateUserProfile(UserProfile userProfile);

    User getUser(UserLogin userLogin);
}
