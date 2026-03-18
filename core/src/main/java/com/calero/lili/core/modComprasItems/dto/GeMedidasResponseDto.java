package com.calero.lili.core.modComprasItems.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GeMedidasResponseDto {

    private UUID idMedida;
    private String medida;
    private Integer factor;

}
