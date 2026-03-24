package com.calero.lili.core.modCompras.modComprasRetenciones.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResponseValoresDto {
    private String codigo;
    private String codigoPorcentaje;
    private BigDecimal baseImponible;
    private BigDecimal valor;
}
