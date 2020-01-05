package com.ht.project.snsproject.service;

import com.ht.project.snsproject.mapper.UserMapper;
import com.ht.project.snsproject.model.UserJoin;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    UserMapper userMapper;

    @Override
    public void join(UserJoin userJoin) {
        userMapper.join(userJoin);
    }
}
