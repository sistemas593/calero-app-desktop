package com.calero.lili.core.tablas.tbSustentos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbSustentosCreationRequestDto {
    private String codigoSustento;
    private String sustento;
}
