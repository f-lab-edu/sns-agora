package com.ht.project.snsproject.model.user;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.sql.Date;

@Value
public class UserProfileParam {

  @NotBlank
  String nickname;

  @NotBlank
  @Email
  String email;

  Date birth;
}
