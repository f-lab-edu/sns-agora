package com.ht.project.snsproject.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

  private Integer id;

  private String userId;

  private String email;

  private String name;

  private String nickname;

  private Date birth;

  private String filePath;

  private String fileName;

}
