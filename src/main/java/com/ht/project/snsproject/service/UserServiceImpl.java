package com.ht.project.snsproject.service;

import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.mapper.UserMapper;
import com.ht.project.snsproject.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {


  @Autowired
  @Qualifier("cacheRedisTemplate")
  private RedisTemplate<String, Object> cacheRedisTemplate;

  @Autowired
  private UserMapper userMapper;

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
    if (userInfo == null) {
      return false;
    }

    if (httpSession.getAttribute("userId") != null) {
      throw new DuplicateRequestException("이미 로그인된 상태입니다.");
    }

    String userId = userInfo.getUserId();

    httpSession.setAttribute("userId", userId);
    cacheRedisTemplate.opsForValue().set("userInfo:"+userId, UserCache.from(userInfo), 30L, TimeUnit.MINUTES);
    return true;
  }

  @Override
  public void logout(HttpSession httpSession) {

    String userId = (String) httpSession.getAttribute("userId");
    httpSession.invalidate();
    cacheRedisTemplate.delete("userInfo:"+userId);
  }

  @Override
  public boolean verifyPassword(String userId, String password) {

    String currentPassword = userMapper.getPassword(userId);
    return currentPassword.equals(password);
  }

  @Override
  public void deleteUser(HttpSession httpSession) {

    String userId = (String) httpSession.getAttribute("userId");
    userMapper.deleteUser(userId);
    cacheRedisTemplate.delete("userInfo:"+userId);
    httpSession.invalidate();

  }

  @Override
  public void updateUserPassword(String userId, UserPassword userPassword) {

    userMapper.updateUserPassword(userId,
            userPassword.getCurrentPassword(), userPassword.getNewPassword());
  }

  @Override
  public UserProfile getUserProfile(String userId) {

    return userMapper.getUserProfile(userId);
  }
}
