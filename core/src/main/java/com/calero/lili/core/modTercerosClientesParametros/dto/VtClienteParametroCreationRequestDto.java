package com.calero.lili.core.modTercerosClientesParametros.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VtClienteParametroCreationRequestDto {

    private Boolean relacionado;
    private Boolean permitirCredito;
    private BigDecimal valorCredito;
    private BigInteger cuotas;
    private BigInteger diasCredito;
    private String formaPago;
    private String codigoPrecio;
    private BigInteger porcentajeDscto;
    private String observaciones;
    private UUID idZona;
    private UUID idVendedor;
    private UUID idTercero;

}
