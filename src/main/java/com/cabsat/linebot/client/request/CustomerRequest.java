package com.cabsat.linebot.client.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRequest {
    @JsonProperty("line_user_id")
    private String lineUserId;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("picture_url")
    private String pictureUrl;
    @JsonProperty("no")
    private String no;
    @JsonProperty("order_code")
    private String orderCode;
    @JsonProperty("name")
    private String name;
    @JsonProperty("address")
    private String address;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("social_name")
    private String socialName;
    @JsonProperty("payment_channel")
    private String paymentChannel;
    @JsonProperty("price")
    private String price;
    @JsonProperty("orders")
    private List<OrderRequest> orderList;
}
