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
public class FormasPagoDto {

    private String formaPago;
    private BigDecimal total;
    private String plazo;
    private String unidadTiempo;

}
