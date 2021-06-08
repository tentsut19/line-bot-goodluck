package com.cabsat.linebot.controller.request;

import lombok.*;

@Data
public class ContactRequest {
    private String mobile;
    private String email;
    private String line;
    private String lineUserId;
}
