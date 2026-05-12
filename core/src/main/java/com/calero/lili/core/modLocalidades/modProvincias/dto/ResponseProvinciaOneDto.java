package com.calero.lili.core.modLocalidades.modProvincias.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseProvinciaOneDto {

    private String provincia;
    private String codigoProvincia;
    private List<ResponseProvinciaCantonDto> cantones;
}
