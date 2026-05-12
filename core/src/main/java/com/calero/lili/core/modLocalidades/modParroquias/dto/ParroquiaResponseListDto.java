package com.calero.lili.core.modLocalidades.modParroquias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParroquiaResponseListDto {

    private String codigoParroquia;
    private String parroquia;

}
