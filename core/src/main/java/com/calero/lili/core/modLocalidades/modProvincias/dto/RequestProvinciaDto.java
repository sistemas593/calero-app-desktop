package com.calero.lili.core.modLocalidades.modProvincias.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestProvinciaDto {

    private String provincia;
    private String codigoProvincia;
}
