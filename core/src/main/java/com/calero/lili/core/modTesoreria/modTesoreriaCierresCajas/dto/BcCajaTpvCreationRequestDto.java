package com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BcCajaTpvCreationRequestDto {

    private String sucursal;
    private String fechaApertura;
    private String fechaCierre;
    private BigDecimal efectivo;
    private BigDecimal cheque;
    private BigDecimal tarjeta;
    private BigDecimal transferencia;
    private BigDecimal retencion;
    private BigDecimal otras;
    private BigDecimal credito;
    private BigDecimal sutilizado;
    private BigDecimal base;
    private BigDecimal iva;
    private BigDecimal ice;
    private BigDecimal propina;
    private BigDecimal saldoUtilizado;

}
