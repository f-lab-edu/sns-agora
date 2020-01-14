package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.UserJoin;
import com.ht.project.snsproject.model.UserProfile;

public interface UserService {

    void joinUser(UserJoin userJoin);

    boolean checkDuplicateUserId(String userId);

    void updateUserProfile(UserProfile userProfile);

}
