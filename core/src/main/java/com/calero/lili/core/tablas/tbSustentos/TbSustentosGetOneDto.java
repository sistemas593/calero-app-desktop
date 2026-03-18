package com.calero.lili.core.tablas.tbSustentos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TbSustentosGetOneDto {
    private String codigoSustento;
    private String sustento;
}
