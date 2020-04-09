package com.ht.project.snsproject.enumeration;

public enum ErrorCode {

    //custom error code
    UPLOAD_ERROR(500, "F001", "Fail file upload");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
