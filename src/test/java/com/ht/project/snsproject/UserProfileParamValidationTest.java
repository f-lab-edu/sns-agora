package com.ht.project.snsproject;

import com.ht.project.snsproject.model.user.UserProfileParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.*;
import java.sql.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserProfileParamValidationTest {

  private Validator validator;

  @BeforeEach
  public void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  private void validateBean(Object bean) throws AssertionError {
    Optional<ConstraintViolation<Object>> violation = validator.validate(bean).stream().findFirst();
    violation.ifPresent(v -> {
      throw new ValidationException(violation.get().getMessage());
    });
  }

  @Test
  @DisplayName("필요한 프로필 정보를 정상적으로 입력하면 성공합니다.")
  public void enterUserProfileParamSuccess() {

    UserProfileParam userProfileParam = new UserProfileParam("test",
            "testuser@gmail.com", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserProfileParam>> violations = validator.validate(userProfileParam);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("잘못된 email형식을 입력하면 ViolationException이 발생합니다.")
  public void enterEmailWithInvalidFormFailed() {

    UserProfileParam userProfileParam = new UserProfileParam("test",
            "testuser1_gmail.com", Date.valueOf("2021-01-01"));

    assertThrows(ValidationException.class, () -> validateBean(userProfileParam));
  }

  @Test
  @DisplayName("email에 공백을 입력하면 ViolationException이 발생합니다.")
  public void enterEmailWithBlankFailed() {

    UserProfileParam userProfileParam = new UserProfileParam("test",
            " ", Date.valueOf("2021-01-01"));

    assertThrows(ValidationException.class, () -> validateBean(userProfileParam));
  }

  @Test
  @DisplayName("email에 null을 입력하면 ViolationException이 발생합니다.")
  public void enterEmailWithNullFailed() {

    UserProfileParam userProfileParam = new UserProfileParam("test",
            null, Date.valueOf("2021-01-01"));

    assertThrows(ValidationException.class, () -> validateBean(userProfileParam));
  }


  @Test
  @DisplayName("email에 빈 문자열을 입력하면 ViolationException이 발생합니다.")
  public void enterEmailWithEmptyStringFailed() {

    UserProfileParam userProfileParam = new UserProfileParam("test",
            "", Date.valueOf("2021-01-01"));

    assertThrows(ValidationException.class, () -> validateBean(userProfileParam));
  }

  @Test
  @DisplayName("nickname에 빈 문자열을 입력하면 ViolationException이 발생합니다.")
  public void enterNicknameWithEmptyStringFailed() {

    UserProfileParam userProfileParam = new UserProfileParam("",
            "testuser@gmail.com", Date.valueOf("2021-01-01"));

    assertThrows(ValidationException.class, () -> validateBean(userProfileParam));
  }

  @Test
  @DisplayName("nickname에 공백 문자를 입력하면 ViolationException이 발생합니다.")
  public void enterNicknameWithBlankFailed() {

    UserProfileParam userProfileParam = new UserProfileParam(" ",
            "testuser@gmail.com", Date.valueOf("2021-01-01"));

    assertThrows(ValidationException.class, () -> validateBean(userProfileParam));
  }

  @Test
  @DisplayName("nickname에 null을 입력하면 ViolationException이 발생합니다.")
  public void enterNicknameWithNullFailed() {

    UserProfileParam userProfileParam = new UserProfileParam(null,
            "testuser@gmail.com", Date.valueOf("2021-01-01"));

    assertThrows(ValidationException.class, () -> validateBean(userProfileParam));
  }
}
