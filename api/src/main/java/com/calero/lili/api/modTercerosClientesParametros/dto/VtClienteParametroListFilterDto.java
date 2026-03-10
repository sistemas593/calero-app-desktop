package com.calero.lili.api.modTercerosClientesParametros.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@ToString
public class VtClienteParametroListFilterDto {

    //private Long idData;

    //private int idEmpresa;

    private String idTercero;

    private String idParametro;

    private Integer idGrupo;

    private Boolean relacionado;

    private Boolean permitirCredito;

    private BigDecimal valorCredito;

    private BigInteger cuotas;

    private BigInteger diasCredito;

    private String formaPago;

    private String codigoPrecio;

    private BigInteger porcentajeDscto;

    private String observaciones;

    private Integer idZona;

    private String idVendedor;

}
