package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.feed.FileForProfile;
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

  /*
  단순히 유저를 가져오는 것이 아닌 인증된 유저의 정보를 가져오는 과정이기 때문에
  메소드명을 getAuthenticatedUser 로 수정.
  */
  User getAuthenticatedUser(UserLogin userLogin);

  FileForProfile getUserProfileImage(String userId);

  User getUserFromUserId(String userId);

  String getPassword(String userId);

  void deleteUser(String userId);

  void updateUserPassword(String userId, String currentPw, String newPw);

  UserProfile getUserProfile(String targetId);
}
