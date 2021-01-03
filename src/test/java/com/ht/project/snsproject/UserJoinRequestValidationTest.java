package com.ht.project.snsproject;

import com.ht.project.snsproject.model.user.UserJoinRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.sql.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserJoinRequestValidationTest {

  private Validator validator;

  @BeforeEach
  public void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("정상적인 userId를 입력하면 통과합니다.")
  public void enterUserIdSuccess() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("userId 첫 문자에 숫자를 입력하면 실패합니다.")
  public void enterUserIdWithNumberForFirstCharacterFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("1testuser1",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("userId에 특수문자를 입력하면 실패합니다.")
  public void enterUserIdWithSpecialCharacterFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("testuser1@",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("userId를 5자 이내로 입력하면 실패합니다.")
  public void enterUserIdWithShortStringFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("test1",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("userId를 20자를 초과해서 입력하면 실패합니다.")
  public void enterUserIdLongerThan20LengthFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("testuserTESTUSER12345",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("userId에 공백을 입력하면 실패합니다.")
  public void enterUserIdWithBlankFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("       ",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("userId에 빈 문자열을 입력하면 실패합니다.")
  public void enterUserIdWithEmptyStringFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("userId 문자열 중간에 공백을 입력하면 실패합니다.")
  public void enterUserIdWithBlankBetweenStringFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("test user",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("password를 정상적으로 입력하면 성공합니다.")
  public void enterPasswordSuccess() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("password에 대문자가 최소 1개이상 입력되지 않으면 실패합니다.")
  public void enterPasswordWithAnyUppercaseCharacterFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("password에 소문자가 최소 1개이상 입력되지 않으면 실패합니다.")
  public void enterPasswordWithAnyLowercaseCharacterFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "TEST1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("password에 숫자가 최소 1개이상 입력되지 않으면 실패합니다.")
  public void enterPasswordWithAnyNumberFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "testpassword@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("password에 등록된 특수문자가 최소 1개 이상 입력되지 않으면 실패합니다.")
  public void enterPasswordWithAnySpecialCharacterFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "test1234",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("password에 등록되지 않은 특수문자가 입력되면 실패합니다.")
  public void enterPasswordWithInvalidSpecialCharacterFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "test1234?",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("password가 7자 이내로 입력되면 실패합니다.")
  public void enterPasswordWithShortStringFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "test12!",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("password가 12자를 초과해서 입력되면 실패합니다.")
  public void enterPasswordLongerThan12Failed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "test1234567890!",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("password가 공백문자가 입력되면 실패합니다.")
  public void enterPasswordWithBlankFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "            ",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("email을 정상적으로 입력하면 성공합니다.")
  public void enterEmailSuccess() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("잘못된 email형식을 입력하면 실패합니다.")
  public void enterEmailWithInvalidFormFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "testuser1_gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("email에 공백을 입력하면 실패합니다.")
  public void enterEmailWithBlankFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            " ",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("email에 빈 문자열을 입력하면 실패합니다.")
  public void enterEmailWithEmptyStringFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("name을 정상적으로 입력하면 성공합니다.")
  public void enterNameSuccess() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("name에 공백문자를 입력하면 실패합니다.")
  public void enterNameWithBlankFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "testuser1@gmail.com",
            " ", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("name에 빈 문자열을 입력하면 실패합니다.")
  public void enterNameWithEmptyStringFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "testuser1@gmail.com",
            "", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("nickname을 정상적으로 입력하면 성공합니다.")
  public void enterNicknameSuccess() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "testuser", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("nickname에 빈 문자열을 입력하면 실패합니다.")
  public void enterNicknameWithEmptyStringFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "testuser1@gmail.com",
            "test", "", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("nickname에 공백 문자를 입력하면 실패합니다.")
  public void enterNicknameWithBlankFailed() {

    UserJoinRequest userJoinRequest = new UserJoinRequest("Testuser1",
            "Test1234@",
            "testuser1@gmail.com",
            "test", " ", Date.valueOf("2021-01-01"));

    Set<ConstraintViolation<UserJoinRequest>> violations = validator.validate(userJoinRequest);
    assertFalse(violations.isEmpty());
  }
}
