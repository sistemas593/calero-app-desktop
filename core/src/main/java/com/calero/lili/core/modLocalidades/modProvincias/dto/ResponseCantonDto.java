package com.calero.lili.core.modLocalidades.modProvincias.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseCantonDto {

    private String codigoCanton;
    private String canton;

}
