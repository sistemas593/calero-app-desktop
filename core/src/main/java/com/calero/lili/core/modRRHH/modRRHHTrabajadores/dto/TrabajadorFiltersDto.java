package com.calero.lili.core.modRRHH.modRRHHTrabajadores.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrabajadorFiltersDto {

    private String filter;
    private String unidadMedida;
}
