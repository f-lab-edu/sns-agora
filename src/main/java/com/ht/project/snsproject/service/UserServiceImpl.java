package com.ht.project.snsproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.mapper.UserMapper;
import com.ht.project.snsproject.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
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

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private RedisCacheService redisCacheService;

  @Autowired
  @Qualifier("cacheStrRedisTemplate")
  private StringRedisTemplate cacheStrRedisTemplate;

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

    cacheRedisTemplate.opsForValue()
            .set(redisCacheService.makeCacheKey(CacheKeyPrefix.USER_INFO, userId),
                    UserCache.from(userInfo),
                    30L, TimeUnit.MINUTES);

    return true;
  }

  @Override
  public User getUserInfoCache(String userId) {

    User userInfo;

    String userInfoKey = redisCacheService.makeCacheKey(CacheKeyPrefix.USER_INFO, userId);

    if (cacheStrRedisTemplate.hasKey(userInfoKey) != null) {

      try {
        userInfo = User.from(
               objectMapper.readValue(
                       cacheStrRedisTemplate.boundValueOps(userInfoKey).get(), UserCache.class));
      } catch (JsonProcessingException e) {
        throw new SerializationException("변환에 실패하였습니다.", e);
      }

    } else {
      userInfo = userMapper.getUserFromUserId(userId);
      cacheRedisTemplate.opsForValue().set(userInfoKey, UserCache.from(userInfo), 30L, TimeUnit.MINUTES);
    }

    return userInfo;
  }

  @Override
  public void logout(HttpSession httpSession) {

    String userId = (String) httpSession.getAttribute("userId");
    httpSession.invalidate();
    cacheRedisTemplate.delete(redisCacheService.makeCacheKey(CacheKeyPrefix.USER_INFO, userId));
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
    cacheRedisTemplate.delete(redisCacheService.makeCacheKey(CacheKeyPrefix.USER_INFO, userId));
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
