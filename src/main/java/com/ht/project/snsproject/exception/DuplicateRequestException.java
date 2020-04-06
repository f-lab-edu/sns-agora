package com.ht.project.snsproject.exception;

/**
 *  중복된 요청이 발생할 때 발생하는 RuntimeException 정의.
 */

public class DuplicateRequestException extends RuntimeException {
  public DuplicateRequestException(String message) {
    super(message);
  }
}

