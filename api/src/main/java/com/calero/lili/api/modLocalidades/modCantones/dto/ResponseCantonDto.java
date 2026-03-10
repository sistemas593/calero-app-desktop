package com.calero.lili.api.modLocalidades.modCantones.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseCantonDto {

    private String canton;
    private String codigoCanton;
    private ResponseProvinciaDto provincia;
}
