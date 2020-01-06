package com.ht.project.snsproject.model;

import lombok.Value;
import java.sql.Date;

@Value
public class UserJoin {

    private String userId;

    private String password;

    private String email;

    private String name;

    private String nickname;

    private Date birth;
}
