package com.calero.lili.core.apiSitac.dtos.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResendWebhookDataDto {

    @JsonProperty("email_id")
    private String emailId;

    @JsonProperty("message_id")
    private String messageId;

    private String from;

    private List<String> to;

    private String subject;

    private ResendBounceDto bounce;

    private ResendFailedDto failed;

}
