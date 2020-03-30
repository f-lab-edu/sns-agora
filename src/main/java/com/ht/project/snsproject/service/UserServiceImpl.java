package com.ht.project.snsproject.service;

import com.google.api.Http;
import com.ht.project.snsproject.Exception.DuplicateRequestException;
import com.ht.project.snsproject.mapper.UserMapper;
import com.ht.project.snsproject.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserMapper userMapper;

    @Override
    public void joinUser(UserJoinRequest userJoinRequest) {
        userMapper.insertUser(userJoinRequest);
    }

    @Override
    public boolean isDuplicateUserId(String userId) {
        return userMapper.isDuplicateUserId(userId);
    }

    @Override
    public void updateUserProfile(UserProfile userProfile) {
        userMapper.updateUserProfile(userProfile);
    }

    @Override
    public boolean existUser(UserLogin userLogin, HttpSession httpSession) {
        User userInfo = userMapper.getUser(userLogin);

        if(userInfo==null){
            return false;
        }

        if(httpSession.getAttribute("userInfo")!=null){
            throw new DuplicateRequestException("이미 로그인된 상태입니다.");
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

    @Override
    public UserProfile getUserProfile(String userId){
        return userMapper.getUserProfile(userId);
    }

}
