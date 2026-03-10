package com.calero.lili.api.modTercerosProvedoresParametros.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@ToString
public class CpProveedorParametroListFilterDto {

    //private Long idData;

   // private int idEmpresa;

    private String idTercero;

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
