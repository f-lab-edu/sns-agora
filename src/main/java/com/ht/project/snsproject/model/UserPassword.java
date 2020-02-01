package com.ht.project.snsproject.model;

import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Value
public class UserPassword {

    @NotNull
    private String currentPassword;

    @NotNull
    @Pattern(regexp = "^(?=.*\\d)(?=.*[~!@#$%^&()_+])(?=.*[a-z])(?=.*[A-Z]).{8,12}$")
    private String newPassword;
}
