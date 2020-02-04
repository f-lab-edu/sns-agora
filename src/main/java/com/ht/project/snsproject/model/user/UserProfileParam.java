package com.ht.project.snsproject.model.user;

import lombok.Value;

import java.sql.Date;

@Value
public class UserProfileParam {

    private String nickname;

    private String email;

    private Date birth;
}
