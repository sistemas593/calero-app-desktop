package com.calero.lili.api.modLocalidades.modCantones.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseProvinciaDto {

    private String provincia;
    private String codigoProvincia;

}
