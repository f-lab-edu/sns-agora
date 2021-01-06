package com.ht.project.snsproject.model.user;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.sql.Date;


@Value
public class UserJoinRequest {

  @NotBlank(message = "아이디는 필수 입력사항입니다.(영문 대소문자, 숫자 6~20자이내, 단, 첫 문자는 영문 대,소문자만 허용)")
  @Pattern(regexp = "^[a-zA-Z]{1}[0-9a-zA-Z]{5,19}$")
  String userId;

  @NotBlank(message = "비밀번호는 필수 입력사항입니다.")
  @Pattern(regexp = "^(?=.*\\d)(?=.*[~!@#$%^&()_+])(?=.*[a-z])(?=.*[A-Z]).{8,12}$",
          message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 각 1개 이상 포함합니다.(~!@#$%^&*()_+)(8~12자 이내)")
  String password;

  @NotBlank
  @Email
  String email;

  @NotBlank
  String name;

  @NotBlank
  String nickname;

  Date birth;
}
