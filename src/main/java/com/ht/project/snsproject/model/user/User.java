package com.ht.project.snsproject.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.sql.Date;


@Builder
@AllArgsConstructor
@Value
public class User {

  Integer id;

  String userId;

  String email;

  String name;

  String nickname;

  Date birth;

  String filePath;

  String fileName;

  public static User from(UserCache userCache){

    return User.builder()
            .id(Integer.valueOf(userCache.getId()))
            .userId(userCache.getUserId())
            .email(userCache.getEmail())
            .name(userCache.getName())
            .nickname(userCache.getNickname())
            .birth(Date.valueOf(userCache.getBirth()))
            .filePath(userCache.getFilePath())
            .fileName(userCache.getFileName())
            .build();
  }
}
