package com.cabsat.linebot.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    @JsonProperty("code")
    private String code;
    @JsonProperty("error_message")
    private String errorMessage;
    @JsonProperty("product_setting_quantity")
    private List<ProductSettingQuantity> productSettingQuantity;
}
