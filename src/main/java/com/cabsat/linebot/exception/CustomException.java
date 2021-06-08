package com.cabsat.linebot.exception;


import org.springframework.http.HttpStatus;

public class CustomException extends Exception {

    private final String errorCode;
    private final String message;
    private HttpStatus httpStatus;

    public CustomException(String errorCode) {
        this.errorCode = errorCode;
        this.message = null;
        this.httpStatus = null;
    }

    public CustomException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = null;
    }

    public CustomException(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    public CustomException(HttpStatus httpStatus, String errorCode) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = null;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public HttpStatus getHttpStatus() {
        if(this.httpStatus == null){
            this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
