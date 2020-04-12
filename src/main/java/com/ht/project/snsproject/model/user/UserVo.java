package com.ht.project.snsproject.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {

  int id;

  String userId;

  String email;

  String name;

  String nickname;

  Date birth;

  public static UserVo create(User user){

    return UserVo.builder()
            .id(user.getId())
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .nickname(user.getNickname())
            .birth(user.getBirth())
            .build();
  }
}
