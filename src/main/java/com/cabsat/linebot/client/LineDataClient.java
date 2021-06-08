package com.cabsat.linebot.client;

import com.cabsat.linebot.client.response.LineProfileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class LineDataClient {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${line.bot.channel-token}")
    private String lineBotChannelToken;

    final String LINE_DATA_HOST = "https://api-data.line.me";
    final String LINE_DATA_ENDPOINT = "/v2/bot/message/{messageId}/content";
    final String LINE_GET_PROFILE_ENDPOINT = "https://api.line.me/v2/bot/profile/{userId}";
    final String LINE_GET_PROFILE_GROUP_ENDPOINT = "https://api.line.me/v2/bot/group/{groupId}/member/{userId}";

    public byte[] getContent(String messageId) {
        String lineEndpoint = LINE_DATA_ENDPOINT.replace("{messageId}", messageId);
        String endpoint = LINE_DATA_HOST + lineEndpoint;
        log.info(
                "Start client for Line API. {} request {}",
                endpoint,messageId
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+lineBotChannelToken);
        HttpEntity httpRequest = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                httpRequest,
                new ParameterizedTypeReference<byte[]>() {
                }
        );
//        log.info("Response Line API., http body : {}", response.getBody());
        log.info("End Line API., http status : {}", response.getStatusCodeValue());
        return response.getBody();
    }

    public LineProfileResponse getProfileGroupEndpoint(String groupId,String userId) {
        String endpoint = LINE_GET_PROFILE_GROUP_ENDPOINT.replace("{groupId}", groupId);
        endpoint = endpoint.replace("{userId}", userId);
        log.info(
                "Start client for Line API. {} request {}",
                endpoint,userId
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+lineBotChannelToken);
        HttpEntity httpRequest = new HttpEntity<>(headers);
        ResponseEntity<LineProfileResponse> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                httpRequest,
                new ParameterizedTypeReference<LineProfileResponse>() {
                }
        );
        log.info("End Line API., http status : {}", response.getStatusCodeValue());
        return response.getBody();
    }

    public LineProfileResponse getProfileEndpoint(String userId) {
        String endpoint = LINE_GET_PROFILE_ENDPOINT.replace("{userId}", userId);
        log.info(
                "Start client for Line API. {} request {}",
                endpoint,userId
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+lineBotChannelToken);
        HttpEntity httpRequest = new HttpEntity<>(headers);
        ResponseEntity<LineProfileResponse> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                httpRequest,
                new ParameterizedTypeReference<LineProfileResponse>() {
                }
        );
        log.info("End Line API., http status : {}", response.getStatusCodeValue());
        return response.getBody();
    }

}
