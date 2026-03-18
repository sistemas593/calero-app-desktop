package com.calero.lili.core.modLocalidades.modProvincias.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProvinceListFiltersDto {

    private String filter;
    private String unidadMedida;
}
