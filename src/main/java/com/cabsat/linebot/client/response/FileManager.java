package com.cabsat.linebot.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileManager {
    @JsonProperty("name_display")
    private String nameDisplay;
    @JsonProperty("name_file")
    private String nameFile;
    @JsonProperty("path")
    private String path;
    @JsonProperty("type")
    private String type;
    @JsonProperty("url")
    private String url;
    @JsonProperty("description")
    private String description;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("order_code")
    private String orderCode;
}
