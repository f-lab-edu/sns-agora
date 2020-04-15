package com.ht.project.snsproject.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCache {

  String id;

  String userId;

  String email;

  String name;

  String nickname;

  String birth;
}
