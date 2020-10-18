package com.ht.project.snsproject.model.user;

import java.io.Serializable;
import java.sql.Date;
import lombok.Value;

@Value
public class User implements Serializable {

  int id;

  String userId;

  String email;

  String name;

  String nickname;

  Date birth;
}
