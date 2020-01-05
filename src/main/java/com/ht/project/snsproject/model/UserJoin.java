package com.ht.project.snsproject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserJoin {
    private String userId;

    private String password;

    private String email;

    private String name;

    private String nickname;

    private Date birth;
}
