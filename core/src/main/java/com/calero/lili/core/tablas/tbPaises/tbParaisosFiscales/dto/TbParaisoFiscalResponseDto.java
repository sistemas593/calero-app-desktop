package com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbParaisoFiscalResponseDto {

    private String codigo;
    private String paraisoFiscal;
    private Pais pais;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pais {
        private String codigoPais;
        private String pais;
    }

}
