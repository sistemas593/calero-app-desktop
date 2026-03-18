package com.calero.lili.core.modLocalidades.modCantones.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestCantonDto {

    private String canton;
    private String codigoCanton;
    private String codigoProvincia;
}
