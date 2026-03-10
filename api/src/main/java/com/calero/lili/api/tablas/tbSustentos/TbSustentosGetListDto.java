package com.calero.lili.api.tablas.tbSustentos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TbSustentosGetListDto {
    private String codigoSustento;
    private String sustento;
}
