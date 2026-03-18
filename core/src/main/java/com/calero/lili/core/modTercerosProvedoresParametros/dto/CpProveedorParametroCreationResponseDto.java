package com.calero.lili.core.modTercerosProvedoresParametros.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;


@Data
@Builder
public class CpProveedorParametroCreationResponseDto {

    private UUID idParametro;
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
