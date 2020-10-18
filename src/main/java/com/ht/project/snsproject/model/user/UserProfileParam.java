package com.ht.project.snsproject.model.user;

import java.sql.Date;
import lombok.Value;

@Value
public class UserProfileParam {

  String nickname;

  String email;

  Date birth;
}
