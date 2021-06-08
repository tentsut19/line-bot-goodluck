package com.cabsat.linebot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
//        return !clientHttpResponse.getStatusCode().is2xxSuccessful();
        return true;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        log.info("Status Code : {}, Response Body : {}",
                clientHttpResponse.getStatusCode(),
                StreamUtils.copyToString(clientHttpResponse.getBody(), UTF_8));
    }

}
