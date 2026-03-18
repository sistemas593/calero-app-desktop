package com.calero.lili.core.modContabilidad.modReportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceComprobacionPdfDto {

    private String codigoCuenta;
    private String cuenta;
    private String saldoInicial;
    private String debe;
    private String haber;
    private String saldoFinal;

}
