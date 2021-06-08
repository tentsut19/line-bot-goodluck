package com.cabsat.linebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Date;


@Service
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledService {

    @Scheduled(cron = "0 0/30 * ? * *")
    public void startService() throws Exception {
        log.info("Scheduled Started at {}",new Date());
    }
}
