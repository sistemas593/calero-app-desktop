package com.calero.lili.core.modCompras.modComprasImpuestos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValoresDto {

    private String codigo;
    private String codigoPorcentaje;
    private BigDecimal tarifa;
    private BigDecimal baseImponible;
    private BigDecimal valor;


}
