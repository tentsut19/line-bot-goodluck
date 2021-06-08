package com.cabsat.linebot.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSettingQuantity {
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("quantity")
    private String quantity;
}
