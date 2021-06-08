package com.cabsat.linebot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageLineResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;
}
