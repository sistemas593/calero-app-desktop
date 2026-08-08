package com.calero.lili.core.apiSitac.dtos.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResendWebhookEventDto {

    private String type;

    @JsonProperty("created_at")
    private String createdAt;

    private ResendWebhookDataDto data;

}
