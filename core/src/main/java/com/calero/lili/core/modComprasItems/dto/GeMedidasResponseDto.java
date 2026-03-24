package com.calero.lili.core.modComprasItems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeMedidasResponseDto {

    private UUID idMedida;
    private String medida;
    private Integer factor;

}
