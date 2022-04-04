package com.cabsat.linebot.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyResponse {
    private Long id;
    private String name;
    @JsonProperty("tax_identification_number")
    private String taxIdentificationNumber;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String email;
}
