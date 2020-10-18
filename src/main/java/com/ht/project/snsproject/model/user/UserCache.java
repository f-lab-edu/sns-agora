package com.ht.project.snsproject.model.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCache {

  private String id;

  private String userId;

  private String email;

  private String name;

  private String nickname;

  private String birth;

  private String filePath;

  private String fileName;

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
