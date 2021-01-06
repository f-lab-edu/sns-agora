package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.ProfileImage;
import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.model.user.UserJoinRequest;
import com.ht.project.snsproject.model.user.UserLogin;
import com.ht.project.snsproject.model.user.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

  void insertUser(UserJoinRequest userJoinRequest);

  boolean isDuplicateUserId(String userId);

  void updateUserProfile(UserProfile userProfile);

  boolean isAuthenticatedUser(UserLogin userLogin);

  ProfileImage findUserProfileImageByUserId(String userId);

  User findUserByUserId(String userId);

  String findPasswordByUserId(String userId);

  void deleteUser(String userId);

  void updateUserPassword(String userId, String currentPw, String newPw);

}
