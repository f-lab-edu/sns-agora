package com.ht.project.snsproject.Exception;

/**
 *  지정된 FriendStatus 가 아닌 이외의 FriendStatus 일 때 메소드에 접근하면
 *  발생하는 오류
 */
public class InvalidApproachException extends RuntimeException {
    public InvalidApproachException(String message) {
        super(message);
    }
}
