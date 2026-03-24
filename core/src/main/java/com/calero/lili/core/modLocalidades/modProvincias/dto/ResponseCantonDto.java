package com.calero.lili.core.modLocalidades.modProvincias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCantonDto {

    private String codigoCanton;
    private String canton;

}
