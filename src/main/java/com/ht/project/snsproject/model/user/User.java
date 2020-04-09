package com.ht.project.snsproject.model.user;


import lombok.Value;

import java.io.Serializable;
import java.sql.Date;

@Value
public class User implements Serializable {

    int id;

    String userId;

    String email;

    String name;

    String nickname;

    Date birth;
}
