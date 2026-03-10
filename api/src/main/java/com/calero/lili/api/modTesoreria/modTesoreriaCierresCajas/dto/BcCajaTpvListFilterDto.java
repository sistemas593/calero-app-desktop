package com.calero.lili.api.modTesoreria.modTesoreriaCierresCajas.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@ToString
public class BcCajaTpvListFilterDto {

   // private Long idData;
   // private int idEmpresa;
    private int idSucursal;
    private int idCajaTpv;
    private Timestamp fechaApertura;
    private Timestamp fechaCierre;
    private BigDecimal efectivo;
    private BigDecimal cheque;
    private BigDecimal tarjeta;
    private BigDecimal transferencia;
    private BigDecimal retencion;
    private BigDecimal otras;
    private BigDecimal credito;
    private BigDecimal sutilizado;
    private BigDecimal basimpng;
    private BigDecimal basimpnob;
    private BigDecimal basimpsg;
    private BigDecimal valiva;
    private BigDecimal valice;
    private BigDecimal propina;
    private BigDecimal tdescuento;


}
