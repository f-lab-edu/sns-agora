package com.ht.project.snsproject.model.user;


import lombok.Value;

import java.io.Serializable;
import java.sql.Date;

@Value
public final class User implements Serializable {

    private int id;

    private String userId;

    private String email;

    private String name;

    private String nickname;

    private Date birth;
}
