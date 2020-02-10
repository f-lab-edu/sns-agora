package com.ht.project.snsproject.Exception;

import com.ht.project.snsproject.enumeration.ErrorCode;

public class FileUploadException extends RuntimeException {

    private final ErrorCode errorCode;

    public FileUploadException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public FileUploadException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FileUploadException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public FileUploadException(String message, Throwable cause, ErrorCode errorCode){
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
