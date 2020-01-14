package com.ht.project.snsproject.service;

import com.ht.project.snsproject.mapper.UserMapper;
import com.ht.project.snsproject.model.UserJoin;
import com.ht.project.snsproject.model.UserProfile;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    UserMapper userMapper;

    @Override
    public void joinUser(UserJoin userJoin) {
        userMapper.joinUser(userJoin);
    }

    @Override
    public boolean checkDuplicateUserId(String userId) {
        return userMapper.checkDuplicateUserId(userId);
    }

    @Override
    public void updateUserProfile(UserProfile userProfile) {
        userMapper.updateUserProfile(userProfile);
    }

}
