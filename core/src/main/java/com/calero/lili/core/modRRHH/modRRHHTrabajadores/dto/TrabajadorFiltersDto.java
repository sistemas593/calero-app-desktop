package com.calero.lili.core.modRRHH.modRRHHTrabajadores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrabajadorFiltersDto {

    private String filter;
    private String unidadMedida;
}
