package com.ht.project.snsproject.model.user;

import lombok.Value;

import java.sql.Date;

@Value
public class UserProfileParam {

    String nickname;

    String email;

    Date birth;
}
