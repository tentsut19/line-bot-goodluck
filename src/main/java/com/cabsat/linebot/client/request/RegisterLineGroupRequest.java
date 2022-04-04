package com.cabsat.linebot.client.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterLineGroupRequest {
    @JsonProperty("group_id")
    private String groupId;
    @JsonProperty("group_name")
    private String groupName;
    @JsonProperty("picture_url")
    private String pictureUrl;
}
