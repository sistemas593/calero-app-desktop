package com.calero.lili.api.modLocalidades.modProvincias.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResponseProvinciaDto {

    private String provincia;
    private String codigoProvincia;
    private List<ResponseCantonDto> cantones;
}
