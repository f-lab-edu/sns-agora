package com.ht.project.snsproject.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCache {

  String id;

  String userId;

  String email;

  String name;

  String nickname;

  String birth;

  String filePath;

  String fileName;

  public static UserCache from(User user) {

    return UserCache.builder()
            .id(String.valueOf(user.getId()))
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .nickname(user.getNickname())
            .birth(String.valueOf(user.getBirth()))
            .filePath(user.getFilePath())
            .fileName(user.getFileName())
            .build();
  }

  public static UserCache updateFrom(UserProfile userProfile, int id, String userName) {

    return UserCache.builder().id(String.valueOf(id))
            .userId(userProfile.getUserId())
            .name(userName)
            .nickname(userProfile.getNickname())
            .email(userProfile.getEmail())
            .birth(String.valueOf(userProfile.getBirth()))
            .filePath(userProfile.getFilePath())
            .fileName(userProfile.getFileName())
            .build();
  }
}
