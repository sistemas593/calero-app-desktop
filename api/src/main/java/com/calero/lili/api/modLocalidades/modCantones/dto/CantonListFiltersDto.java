package com.calero.lili.api.modLocalidades.modCantones.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CantonListFiltersDto {

    private String filter;
    private String unidadMedida;
}
