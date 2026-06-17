package com.calero.lili.core.modContabilidad.modAsientos.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResponseAsientoValoresDto {
    private String codigo;
    private String codigoPorcentaje;
    private BigDecimal baseImponible;
    private BigDecimal valor;
}
