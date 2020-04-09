package com.ht.project.snsproject.model.user;

import java.sql.Date;
import lombok.Value;

@Value
public class UserProfile {

  int id;

  String nickname;

  String email;

  Date birth;
}
