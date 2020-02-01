package com.ht.project.snsproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.ht.project.snsproject.mapper.UserMapper;
import com.ht.project.snsproject.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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

    // jetbrain notnull annotation
    @Override
    public boolean getUser(UserLogin userLogin, HttpSession httpSession) {
        User userInfo = userMapper.getUser(userLogin);

        if(userInfo==null){
            return false;
        }
        httpSession.setAttribute("userInfo", userInfo);
        return true;
    }

    @Override
    public boolean verifyPassword(String userId, String password) {
        return userMapper.verifyPassword(userId, password);
    }

    @Override
    public void deleteUser(String userId) {
        userMapper.deleteUser(userId);
    }

    @Override
    public void updateUserPassword(String userId, UserPassword userPassword) {
        userMapper.updateUserPassword(userId, userPassword.getCurrentPassword(), userPassword.getNewPassword());
    }

}
