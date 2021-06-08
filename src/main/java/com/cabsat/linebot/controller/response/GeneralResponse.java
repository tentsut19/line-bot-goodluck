package com.cabsat.linebot.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class GeneralResponse<T> implements Serializable {
    @JsonProperty("code")
    private String code;

    @JsonProperty("data")
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
