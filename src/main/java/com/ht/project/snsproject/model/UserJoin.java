package com.ht.project.snsproject.model;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.sql.Date;

@Value
public class UserJoin {

    @NotBlank(message = "아이디는 필수 입력사항입니다.(영문 소문자, 숫자 6~20자이내)")
    @Pattern(regexp = "^[0-9a-z].{6,20}$")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력사항입니다.")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[~!@#$%^&()_+])(?=.*[a-z])(?=.*[A-Z]).{8,12}$",
            message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 각 1개 이상 포함합니다.(~!@#$%^&*()_+)(8~12자 이내)")
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    private Date birth;
}
