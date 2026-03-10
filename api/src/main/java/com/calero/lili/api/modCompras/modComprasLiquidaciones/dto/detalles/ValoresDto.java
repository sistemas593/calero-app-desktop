package com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.detalles;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ValoresDto {
    private String codigo;
    private String codigoPorcentaje;
    private BigDecimal tarifa;
    private BigDecimal baseImponible;
    private BigDecimal valor;
}
