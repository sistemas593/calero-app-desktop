package com.calero.lili.core.modLocalidades.modCantones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCantonDto {

    private String canton;
    private String codigoCanton;
    private String codigoProvincia;
    private String provincia;
}
