package com.ht.project.snsproject.model.user;

import java.sql.Date;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Value;


@Value
public class UserJoinRequest {

  @NotBlank(message = "아이디는 필수 입력사항입니다.(영문 소문자, 숫자 6~20자이내)")
  @Pattern(regexp = "^[0-9a-z].{6,20}$")
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
