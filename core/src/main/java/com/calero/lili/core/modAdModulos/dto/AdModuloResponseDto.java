package com.calero.lili.core.modAdModulos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdModuloResponseDto {

    private Long idModuloData;
    private String modulo;
}
