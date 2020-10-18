package com.ht.project.snsproject.model.user;

import javax.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class UserLogin {

  @NotBlank(message = "아이디를 입력하세요.")
  String userId;

  @NotBlank(message = "비밀번호를 입력하세요.")
  String password;
}
