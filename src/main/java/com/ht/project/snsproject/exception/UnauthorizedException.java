package com.ht.project.snsproject.exception;

/**
 로그인 인증이 되어있지 않을 때 발생하는 RuntimeException 정의.
 */
public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException(String message) {
    super(message);
  }
}