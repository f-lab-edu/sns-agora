package com.ht.project.snsproject;

import com.ht.project.snsproject.model.user.UserLogin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.*;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserLoginValidationTest {

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
  @DisplayName("정상적인 userId를 입력하면 통과합니다.")
  public void enterUserIdSuccess() {

    UserLogin userLogin = new UserLogin("Testuser1",
            "Test1234@");

    Set<ConstraintViolation<UserLogin>> violations = validator.validate(userLogin);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("userId에 공백을 입력하면 ViolationException이 발생합니다.")
  public void enterUserIdWithBlankFailed() {

    UserLogin userLogin = new UserLogin(" ",
            "Test1234@");

    assertThrows(ValidationException.class, () -> validateBean(userLogin));
  }

  @Test
  @DisplayName("userId에 null을 입력하면 ViolationException이 발생합니다.")
  public void enterUserIdWithNullFailed() {

    UserLogin userLogin = new UserLogin(null,
            "Test1234@");

    assertThrows(ValidationException.class, () -> validateBean(userLogin));
  }

  @Test
  @DisplayName("userId에 빈 문자열을 입력하면 ViolationException이 발생합니다.")
  public void enterUserIdWithEmptyStringFailed() {

    UserLogin userLogin = new UserLogin("",
            "Test1234@");

    assertThrows(ValidationException.class, () -> validateBean(userLogin));
  }

  @Test
  @DisplayName("password를 정상적으로 입력하면 성공합니다.")
  public void enterPasswordSuccess() {


    UserLogin userLogin = new UserLogin("Testuser1",
            "Test1234@");

    Set<ConstraintViolation<UserLogin>> violations = validator.validate(userLogin);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("password가 공백문자가 입력되면 ViolationException이 발생합니다.")
  public void enterPasswordWithBlankFailed() {

    UserLogin userLogin = new UserLogin("Testuser1",
            " ");

    assertThrows(ValidationException.class, () -> validateBean(userLogin));
  }

  @Test
  @DisplayName("password가 빈 문자열이 입력되면 ViolationException이 발생합니다.")
  public void enterPasswordWithEmptyStringFailed() {

    UserLogin userLogin = new UserLogin("Testuser1",
            "");

    assertThrows(ValidationException.class, () -> validateBean(userLogin));
  }

  @Test
  @DisplayName("password가 null이 입력되면 ViolationException이 발생합니다.")
  public void enterPasswordWithNullFailed() {

    UserLogin userLogin = new UserLogin("Testuser1",
            null);

    assertThrows(ValidationException.class, () -> validateBean(userLogin));
  }
}
