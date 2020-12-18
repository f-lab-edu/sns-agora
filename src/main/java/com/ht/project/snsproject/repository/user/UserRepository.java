package com.ht.project.snsproject.repository.user;

import com.ht.project.snsproject.mapper.UserMapper;
import com.ht.project.snsproject.model.feed.ProfileImage;
import com.ht.project.snsproject.model.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final UserMapper userMapper;

  public void insertUser(UserJoinRequest userJoinRequest) {

    userMapper.insertUser(userJoinRequest);
  }

  public boolean isDuplicateUserId(String userId) {

    return userMapper.isDuplicateUserId(userId);
  }

  @Cacheable(value = "userInfo", key = "'userInfo:' + #userId")
  public Object findUserByUserId(String userId) {

    return userMapper.findUserByUserId(userId);
  }

  public ProfileImage findUserProfileImage(String userId) {

    return userMapper.findUserProfileImageByUserId(userId);
  }

  @CacheEvict(value = "userInfo", key = "'userInfo:' + #userProfile.userId")
  public void updateUserProfile(UserProfile userProfile) {

    userMapper.updateUserProfile(userProfile);
  }

  public String findPasswordByUserId(String userId) {

    return userMapper.findPasswordByUserId(userId);
  }

  @CacheEvict(value = "userInfo", key = "'userInfo:' + #userId")
  public void deleteUser(String userId) {

    userMapper.deleteUser(userId);
  }

  public boolean isAuthenticatedUser(UserLogin userLogin) {

    return userMapper.isAuthenticatedUser(userLogin);
  }

  public void updateUserPassword(String userId, UserPassword userPassword) {

    userMapper.updateUserPassword(userId,
            userPassword.getCurrentPassword(), userPassword.getNewPassword());
  }

}
