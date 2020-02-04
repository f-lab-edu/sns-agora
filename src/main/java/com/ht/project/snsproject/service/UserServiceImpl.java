package com.ht.project.snsproject.service;

import com.ht.project.snsproject.mapper.UserMapper;
import com.ht.project.snsproject.model.user.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    UserMapper userMapper;

    @Override
    public void joinUser(UserJoin userJoin) {
        userMapper.joinUser(userJoin);
    }

    @Override
    public boolean IsDuplicateUserId(String userId) {
        return userMapper.checkDuplicateUserId(userId);
    }

    @Override
    public void updateUserProfile(UserProfile userProfile) {
        userMapper.updateUserProfile(userProfile);
    }

    // jetbrain notnull annotation
    @Override
    public boolean existUser(UserLogin userLogin, HttpSession httpSession) {
        User userInfo = userMapper.getUser(userLogin);

        if(userInfo==null){
            return false;
        }
        httpSession.setAttribute("userInfo", userInfo);
        return true;
    }

    @Override
    public boolean verifyPassword(String userId, String password) {
        String currentPassword = userMapper.getPassword(userId);
        return currentPassword.equals(password);
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
