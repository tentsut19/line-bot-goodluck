package com.cabsat.linebot.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSettingResponse {
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("cost")
    private BigDecimal cost;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("format_product_name")
    private Boolean formatProductName;
    @JsonProperty("format_product_quantity")
    private Boolean formatProductQuantity;
    @JsonProperty("format_product_color")
    private Boolean formatProductColor;
    @JsonProperty("format_product_size")
    private Boolean formatProductSize;
}
