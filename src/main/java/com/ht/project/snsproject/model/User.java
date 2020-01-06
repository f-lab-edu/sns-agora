package com.ht.project.snsproject.model;


import lombok.Value;
import java.sql.Date;

@Value
public final class User {

    private int id;

    private String userId;

    private String password;

    private String email;

    private String name;

    private String nickname;

    private Date birth;
}
