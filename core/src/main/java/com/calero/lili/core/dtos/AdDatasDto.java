package com.calero.lili.core.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdDatasDto {
    private Long idData;
    private String data;
}
