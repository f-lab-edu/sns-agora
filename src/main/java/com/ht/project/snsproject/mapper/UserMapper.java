package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.user.User;
import com.ht.project.snsproject.model.user.UserJoin;
import com.ht.project.snsproject.model.user.UserLogin;
import com.ht.project.snsproject.model.user.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void joinUser(UserJoin userJoin);

    boolean checkDuplicateUserId(String userId);

    void updateUserProfile(UserProfile userProfile);

    User getUser(UserLogin userLogin);

    String getPassword(String userId);

    void deleteUser(String userId);

    void updateUserPassword(String userId, String currentPw, String newPw);

}
