package com.cabsat.linebot.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderSummaryResponse {
    @JsonProperty("code")
    private String code;
    @JsonProperty("payment_channel")
    private String paymentChannel;
    @JsonProperty("total_amount")
    private String totalAmount;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("product_draft_name")
    private String productDraftName;
    @JsonProperty("create_date")
    private String createDate;
}
