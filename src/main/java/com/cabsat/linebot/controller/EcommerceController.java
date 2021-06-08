package com.cabsat.linebot.controller;

import com.cabsat.linebot.client.response.CustomerResponse;
import com.cabsat.linebot.controller.request.ContactRequest;
import com.cabsat.linebot.controller.service.CabsatService;
import com.cabsat.linebot.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EcommerceController {

    private final CabsatService cabsatService;

}