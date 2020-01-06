package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.UserJoin;

public interface UserService {

    void join(UserJoin userJoin);

    int userIdCheck(String userId);
}
