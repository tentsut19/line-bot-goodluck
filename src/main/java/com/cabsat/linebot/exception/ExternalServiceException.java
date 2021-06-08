package com.cabsat.linebot.exception;

import org.springframework.http.HttpStatus;


public class ExternalServiceException extends CustomException {

    public ExternalServiceException(String errorCode) {
        super(HttpStatus.BAD_GATEWAY, errorCode);
    }

    public ExternalServiceException(HttpStatus httpStatus, String errorCode) {
        super(httpStatus, errorCode);
    }

    }


