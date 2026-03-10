package com.calero.lili.api.modTercerosProvedoresParametros.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;


@Data
public class CpProveedorParametroReportDto {

    //private Long idData;

   // private Long idEmpresa;

    private Long idTercero;

    private String formaPago;

    private Boolean permiteCredito;

    private BigDecimal valorCredito;

    private BigInteger cuotas;

    private BigInteger diasCredito;

    private String codret;

    private String ideiva;

    private String formaPagoSri;

    private String observaciones;

    private String pnatural;

    private String retiva;

    private String retir;
    private String conconta;
    private String titulosup;
    private String idGrupo;
    private String contesp;
    private String excretiva;
    private String tipoprov;
    private Boolean relacionado;

}
