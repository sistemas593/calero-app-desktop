package com.calero.lili.api.tablas.tbPaises;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TbPaisGetOneDto {
    private String codigoPais;
    private String pais;
}
