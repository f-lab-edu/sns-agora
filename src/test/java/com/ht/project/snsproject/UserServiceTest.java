package com.ht.project.snsproject;

import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.model.user.UserLogin;
import com.ht.project.snsproject.repository.user.UserRepository;
import com.ht.project.snsproject.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks
  private UserServiceImpl userService;

  @Mock
  private UserRepository userRepository;

  private MockHttpSession mockHttpSession;

  private UserLogin userLogin;

  private static final String userId = "testuser1";
  private static final String password = "Test1234!";

  @BeforeEach
  public void setUp() {

    mockHttpSession = new MockHttpSession();
    userLogin = new UserLogin(userId, password);
  }

  @Test
  @DisplayName("요청받은 사용자가 존재하면 인증을 성공합니다.")
  public void existsUserShouldPassed() {

    Mockito.when((userRepository.isAuthenticatedUser(userLogin))).thenReturn(true);
    userService.exists(userLogin, mockHttpSession);

    assertEquals(userId, mockHttpSession.getAttribute("userId"));

  }

  @Test
  @DisplayName("중복된 로그인 요청이 들어오면 DuplicateRequestException이 발생합니다.")
  public void DuplicatedLoginRequestThrownException() {

    mockHttpSession.setAttribute("userId", userId);

    assertThrows(DuplicateRequestException.class,
            () -> ReflectionTestUtils.invokeMethod(userService, "exists", userLogin, mockHttpSession));

  }

  @Test
  @DisplayName("해당 메소드는 회원 등록이 되지 않은 사용자가 요청을 했을 시에 IllegalArgumentException을 발생합니다.")
  public void noExistsUserRequestThrownException() {

    Mockito.when((userRepository.isAuthenticatedUser(userLogin))).thenReturn(false);

    assertThrows(IllegalArgumentException.class,
            () -> ReflectionTestUtils.invokeMethod(userService, "exists", userLogin, mockHttpSession));
  }
  @Test
  @DisplayName("사용자가 올바른 패스워드를 입력하면 삭제요청이 통과합니다.")
  public void authenticatedUserDeleteRequestShouldBePassed() {

    Mockito.when(userRepository.findPasswordByUserId(any(String.class))).thenReturn(password);

    userService.deleteUser(userId, password, mockHttpSession);
  }
  @Test
  @DisplayName("입력한 패스워드가 일치하지 않는 사용자가 탈퇴 요청을 하면 IllegalArgumentException이 발생합니다.")
  public void notAuthenticatedUserDeleteRequestThrownException() {

    Mockito.when(userRepository.findPasswordByUserId(any(String.class))).thenReturn("NoMatched!");

    assertThrows(IllegalArgumentException.class,
            () -> ReflectionTestUtils.invokeMethod(userService, "deleteUser", userId, password, mockHttpSession));

  }
}
