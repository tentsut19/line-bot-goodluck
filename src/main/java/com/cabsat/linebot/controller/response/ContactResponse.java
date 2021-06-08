package com.cabsat.linebot.controller.response;

import lombok.*;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContactResponse {
    private Long id;
    private String mobile;
    private String email;
    private String fax;
    private String website;
    private String line;
    private String lineUserId;
    private String contactType;
    private String facebook;
    private String twitter;
    private String ig;
    private Date deletedAt;
    private Date createdDate;
    private String createdBy;
    private Date updatedDate;
    private String updatedBy;
}
