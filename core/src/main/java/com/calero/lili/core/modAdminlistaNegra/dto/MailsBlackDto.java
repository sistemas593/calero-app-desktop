package com.calero.lili.core.modAdminlistaNegra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailsBlackDto {

    private Long id;
    private String subject;
    private String sender;
    private String recipient;
    @JsonProperty("send_time")
    private String sendTime;
    private String status;
    private String domain;
    private String error;
    @JsonProperty("recipient_domain")
    private String recipientDomain;
}
