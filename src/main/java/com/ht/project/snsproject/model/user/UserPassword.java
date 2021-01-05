package com.ht.project.snsproject.model.user;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Value
public class UserPassword {

  @NotBlank
  String currentPassword;

  @NotBlank
  @Pattern(regexp = "^(?=.*\\d)(?=.*[~!@#$%^&()_+])(?=.*[a-z])(?=.*[A-Z]).{8,12}$")
  String newPassword;
}
