package com.cabsat.linebot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LineController {

    @GetMapping(value = "/test/omise")
    public ResponseEntity<MessageLineResponse> testOmise() {
        log.info("========= testOmise =========");
        return ResponseEntity.ok(MessageLineResponse.builder().status("success").message("test").build());
    }

}