package com.calero.lili.api.modLocalidades.modProvincias.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestProvinciaDto {

    private String provincia;
    private String codigoProvincia;
}
