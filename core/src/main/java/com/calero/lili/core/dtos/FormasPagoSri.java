package com.calero.lili.core.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormasPagoSri {

    private String formaPago;
    private BigDecimal total;
    private String plazo;
    private String unidadTiempo;

}
