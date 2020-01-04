package com.ht.project.snsproject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private int num;

    private String id;

    private String password;

    private String email;

    private String name;

    private String nickname;

    private Date birth;
}
