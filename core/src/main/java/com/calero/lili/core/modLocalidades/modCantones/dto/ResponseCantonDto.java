package com.calero.lili.core.modLocalidades.modCantones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCantonDto {

    private String canton;
    private String codigoCanton;
    private List<ResponseCantonParroquiaDto> parroquias;
}
