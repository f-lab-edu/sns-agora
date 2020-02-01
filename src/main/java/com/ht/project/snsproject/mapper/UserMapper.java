package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.User;
import com.ht.project.snsproject.model.UserJoin;
import com.ht.project.snsproject.model.UserLogin;
import com.ht.project.snsproject.model.UserProfile;
import org.apache.ibatis.annotations.Mapper;

import javax.validation.constraints.NotNull;

@Mapper
public interface UserMapper {

    void joinUser(UserJoin userJoin);

    boolean checkDuplicateUserId(String userId);

    void updateUserProfile(UserProfile userProfile);

    User getUser(UserLogin userLogin);

    boolean verifyPassword(String userId, String password);

    void deleteUser(String userId);

    void updateUserPassword(String userId, String currentPw, String newPw);

}
