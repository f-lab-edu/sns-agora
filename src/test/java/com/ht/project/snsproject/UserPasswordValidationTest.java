package com.ht.project.snsproject;

import com.ht.project.snsproject.model.user.UserPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.*;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserPasswordValidationTest {

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
  @DisplayName("현재 비밀번호와 새로운 비밀번호를 정상적으로 입력하면 성공합니다.")
  public void enterPasswordSuccess() {

    UserPassword userPassword = new UserPassword("Test1234@", "Test1234@");

    Set<ConstraintViolation<UserPassword>> violations = validator.validate(userPassword);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("현재 비밀번호에 공백문자가 입력되면 ViolationException이 발생합니다.")
  public void enterCurrentPasswordWithBlankFailed() {

    UserPassword userPassword = new UserPassword(" ",
            "Test1234@");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("현재 비밀번호에 빈 문자열이 입력되면 ViolationException이 발생합니다.")
  public void enterCurrentPasswordWithEmptyStringFailed() {

    UserPassword userPassword = new UserPassword("",
            "Test1234@");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("현재 비밀번호에 null이 입력되면 ViolationException이 발생합니다.")
  public void enterCurrentPasswordWithNullFailed() {

    UserPassword userPassword = new UserPassword(null,
            "Test1234@");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("새로운 비밀번호에 공백문자가 입력되면 ViolationException이 발생합니다.")
  public void enterNewPasswordWithBlankFailed() {

    UserPassword userPassword = new UserPassword(
            "Test1234@", " ");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("새로운 비밀번호에 빈 문자열이 입력되면 ViolationException이 발생합니다.")
  public void enterNewPasswordWithEmptyStringFailed() {

    UserPassword userPassword = new UserPassword(
            "Test1234@", "");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("새로운 비밀번호에 null이 입력되면 ViolationException이 발생합니다.")
  public void enterNewPasswordWithNullFailed() {

    UserPassword userPassword = new UserPassword(
            "Test1234@", null);

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("새로운 비밀번호에 대문자가 최소 1개이상 입력되지 않으면 ViolationException이 발생합니다.")
  public void enterNewPasswordWithAnyUppercaseCharacterFailed() {

    UserPassword userPassword = new UserPassword("Test1234@", "test1234@");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("새로운 비밀번호에 소문자가 최소 1개이상 입력되지 않으면 ViolationException이 발생합니다.")
  public void enterNewPasswordWithAnyLowercaseCharacterFailed() {

    UserPassword userPassword = new UserPassword("Test1234@", "TEST1234@");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("새로운 비밀번호에 숫자가 최소 1개이상 입력되지 않으면 ViolationException이 발생합니다.")
  public void enterNewPasswordWithAnyNumberFailed() {

    UserPassword userPassword = new UserPassword("Test1234@", "testpassword@");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("새로운 비밀번호에 등록된 특수문자가 최소 1개 이상 입력되지 않으면 ViolationException이 발생합니다.")
  public void enterNewPasswordWithAnySpecialCharacterFailed() {

    UserPassword userPassword = new UserPassword("Test1234@", "test1234");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("새로운 비밀번호에 등록되지 않은 특수문자가 입력되면 ViolationException이 발생합니다.")
  public void enterNewPasswordWithInvalidSpecialCharacterFailed() {

    UserPassword userPassword = new UserPassword("Test1234@", "test1234?");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("새로운 비밀번호가 7자 이내로 입력되면 ViolationException이 발생합니다.")
  public void enterNewPasswordWithShortStringFailed() {

    UserPassword userPassword = new UserPassword("Test1234@", "test12!");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }

  @Test
  @DisplayName("새로운 비밀번호가 12자를 초과해서 입력되면 ViolationException이 발생합니다.")
  public void enterNewPasswordLongerThan12Failed() {

    UserPassword userPassword = new UserPassword("Test1234@", "test1234567890!");

    assertThrows(ValidationException.class, () -> validateBean(userPassword));
  }
}
