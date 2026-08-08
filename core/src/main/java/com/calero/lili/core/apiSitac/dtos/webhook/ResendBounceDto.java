package com.calero.lili.core.apiSitac.dtos.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResendBounceDto {

    private String message;
    private String subType;
    private String type;

}
