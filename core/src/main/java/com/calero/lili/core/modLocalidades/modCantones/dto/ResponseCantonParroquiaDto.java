package com.calero.lili.core.modLocalidades.modCantones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCantonParroquiaDto {

    private String codigoParroquia;
    private String parroquia;

}
