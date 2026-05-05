package com.calero.lili.core.modLocalidades.modParroquias.dto;

import com.calero.lili.core.modLocalidades.modCantones.dto.ResponseCantonDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParroquiaResponseDto {

    private String codigoParroquia;
    private String parroquia;
    private ResponseCantonDto canton;

}
