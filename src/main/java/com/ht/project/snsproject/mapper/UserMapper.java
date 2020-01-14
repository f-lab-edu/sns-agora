package com.ht.project.snsproject.mapper;

import com.ht.project.snsproject.model.UserJoin;
import com.ht.project.snsproject.model.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void joinUser(UserJoin userJoin);

    boolean checkDuplicateUserId(String userId);

    void updateUserProfile(UserProfile userProfile);

}
