package com.ht.project.snsproject.model.user;

import com.ht.project.snsproject.model.feed.ProfileImage;
import lombok.Builder;
import lombok.Value;

import java.sql.Date;

@Value
@Builder
public class UserProfile {

  String userId;

  String nickname;

  String email;

  Date birth;

  String filePath;

  String fileName;

  public static UserProfile from(UserProfileParam userProfileParam,
                                 String userId,
                                 ProfileImage profileImage) {

    return UserProfile.builder()
            .userId(userId)
            .nickname(userProfileParam.getNickname())
            .email(userProfileParam.getEmail())
            .birth(userProfileParam.getBirth())
            .filePath(profileImage.getFilePath())
            .fileName(profileImage.getFileName())
            .build();
  }
}
