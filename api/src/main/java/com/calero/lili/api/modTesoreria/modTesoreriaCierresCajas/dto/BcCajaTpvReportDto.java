package com.calero.lili.api.modTesoreria.modTesoreriaCierresCajas.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
public class BcCajaTpvReportDto {
    private String idSucursal;
    private UUID idCajaTpv;
    private LocalDate fechaApertura;
    private LocalDate fechaCierre;
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
    private BigDecimal valice;
    private BigDecimal propina;

}
