package com.ht.project.snsproject.model.user;

import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Value
public class UserPassword {

    @NotNull
    String currentPassword;

    @NotNull
    @Pattern(regexp = "^(?=.*\\d)(?=.*[~!@#$%^&()_+])(?=.*[a-z])(?=.*[A-Z]).{8,12}$")
    String newPassword;
}
