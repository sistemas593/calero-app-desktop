package com.calero.lili.api.modLocalidades.modProvincias.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseCantonDto {

    private String codigoCanton;
    private String canton;

}
