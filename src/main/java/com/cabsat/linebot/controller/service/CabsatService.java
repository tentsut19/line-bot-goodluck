package com.cabsat.linebot.controller.service;

import com.cabsat.linebot.client.request.CustomerRequest;
import com.cabsat.linebot.client.response.CustomerResponse;
import com.cabsat.linebot.client.EcommerceClient;
import com.cabsat.linebot.controller.request.ContactRequest;
import com.cabsat.linebot.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.cabsat.linebot.constant.ResponseConstant.GENERAL_ERROR_CODE;

@Service
@Slf4j
@RequiredArgsConstructor
public class CabsatService {

    private final EcommerceClient ecommerceClient;
    
}
