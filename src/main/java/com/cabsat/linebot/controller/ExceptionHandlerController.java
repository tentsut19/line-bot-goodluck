package com.cabsat.linebot.controller;


import com.cabsat.linebot.exception.ExternalServiceException;
import com.cabsat.linebot.factory.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.cabsat.linebot.constant.ResponseConstant.GENERAL_ERROR_CODE;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @Autowired
    private ResponseFactory responseFactory;

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleAllCheckedException(Exception e) {
        log.error(e.getMessage(), e);
        return responseFactory.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                GENERAL_ERROR_CODE
        );
    }

    @ResponseBody
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity handleExternalServiceException(ExternalServiceException e) {
        log.error("ExternalServiceException {} - {}", e.getErrorCode(), e.getMessage());
        return responseFactory.error(
                e.getHttpStatus(),
                e.getErrorCode()
        );
    }
}

