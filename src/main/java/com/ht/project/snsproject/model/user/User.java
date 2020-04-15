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

  public static User create(UserCache userCache){

    return User.builder()
            .id(Integer.parseInt(userCache.getId()))
            .userId(userCache.getUserId())
            .email(userCache.getEmail())
            .name(userCache.getName())
            .nickname(userCache.getNickname())
            .birth(new Date(Long.parseLong(userCache.getBirth())))
            .build();
  }
}
