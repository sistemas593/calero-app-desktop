package com.calero.lili.api.modComprasOrden.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ResponseValoresDto {
    private String codigo;
    private String codigoPorcentaje;
    private BigDecimal tarifa;
    private BigDecimal baseImponible;
    private BigDecimal valor;
}
