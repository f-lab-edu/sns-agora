package com.ht.project.snsproject.model.user;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.sql.Date;

@Value
public class UserProfileParam {

  @NotBlank
  String nickname;

  @NotBlank
  String email;

  Date birth;
}
