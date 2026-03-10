package com.calero.lili.api.modVentas.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ResponseValoresDto {
    private String codigo;
    private String codigoPorcentaje;
    private BigDecimal baseImponible;
    private BigDecimal valor;
}
