package com.cabsat.linebot.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderSummary {
    private String productName;
    private List<TransferSummary> transferSummaryList;
    private String transferCount;
    private String transferCountPrice;
    private List<CodSummary> codSummaryList;
    private String codCount;
    private String codCountPrice;
    private String orderSummary;
    private String productSummary;
    private String totalSales;
}
