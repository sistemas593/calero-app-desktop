package com.calero.lili.core.tablas.tbPaises;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbPaisGetListDto {
    private String codigoPais;
    private String pais;
}
