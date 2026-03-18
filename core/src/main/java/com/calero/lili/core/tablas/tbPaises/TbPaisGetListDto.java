package com.calero.lili.core.tablas.tbPaises;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TbPaisGetListDto {
    private String codigoPais;
    private String pais;
}
