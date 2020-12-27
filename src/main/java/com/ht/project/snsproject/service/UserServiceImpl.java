package com.ht.project.snsproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.enumeration.CacheKeyPrefix;
import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.mapper.UserMapper;
import com.ht.project.snsproject.model.feed.FileForProfile;
import com.ht.project.snsproject.model.user.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {


  private final RedisTemplate<String, Object> cacheRedisTemplate;

  private final UserMapper userMapper;

  private final FileService fileService;

  private final ObjectMapper objectMapper;

  private final RedisCacheService redisCacheService;

  private final StringRedisTemplate cacheStrRedisTemplate;

  public UserServiceImpl(@Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> cacheRedisTemplate,
                         UserMapper userMapper,
                         @Qualifier("awsFileService") FileService fileService,
                         ObjectMapper objectMapper,
                         RedisCacheService redisCacheService,
                         @Qualifier("cacheStrRedisTemplate") StringRedisTemplate cacheStrRedisTemplate) {
    this.cacheRedisTemplate = cacheRedisTemplate;
    this.userMapper = userMapper;
    this.fileService = fileService;
    this.objectMapper = objectMapper;
    this.redisCacheService = redisCacheService;
    this.cacheStrRedisTemplate = cacheStrRedisTemplate;
  }

  @Override
  public void joinUser(UserJoinRequest userJoinRequest) {
    userMapper.insertUser(userJoinRequest);
  }

  @Override
  public boolean isDuplicateUserId(String userId) {
    return userMapper.isDuplicateUserId(userId);
  }

  @Override
  public void updateUserProfile(UserProfileParam userProfileParam, String userId, MultipartFile profile) {

    deleteUserProfileImage(userId);

    FileForProfile fileForProfile = fileService.fileUploadForProfile(profile, userId);

    UserProfile userProfile = UserProfile.from(userProfileParam, userId, fileForProfile);

    userMapper.updateUserProfile(userProfile);

    updateUserCache(userProfile);

  }

  private void updateUserCache(UserProfile userProfile) {

    String userCacheKey = redisCacheService.makeCacheKey(CacheKeyPrefix.USER_INFO, userProfile.getUserId());

    User user = getUserInfoCache(userProfile.getUserId());

    cacheRedisTemplate.opsForValue().setIfPresent(userCacheKey,
            UserCache.updateFrom(userProfile, user.getId(), user.getName()), 30L, TimeUnit.MINUTES);

  }

  public void deleteUserProfileImage(String userId) {

    FileForProfile fileForProfile = userMapper.getUserProfileImage(userId);

    if (fileForProfile != null) {
      fileService.deleteFile(fileForProfile.getFilePath(), fileForProfile.getFileName());
    }
  }

  @Override
  public boolean existUser(UserLogin userLogin, HttpSession httpSession) {

    User userInfo = userMapper.getAuthenticatedUser(userLogin);
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

    Boolean keyPresence = cacheStrRedisTemplate.hasKey(userInfoKey);

    if ((keyPresence != null) && (keyPresence)) {

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
