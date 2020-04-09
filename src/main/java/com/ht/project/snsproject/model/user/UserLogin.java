package com.ht.project.snsproject.model.user;

import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class UserLogin {

    @NotBlank(message = "아이디를 입력하세요.")
    String userId;

    @NotBlank(message = "비밀번호를 입력하세요.")
    String password;
}
