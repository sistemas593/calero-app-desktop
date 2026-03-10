package com.calero.lili.api.modCxC.XcFacturas.dto;

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
public class ResponseXcFacturasDto {

    private UUID idFactura;
    private String sucursal;
    private String periodo;
    private String tipoDocumento;
    private String serie;
    private String secuencial;
    private String fechaEmision;
    private String fechaVencimiento;
    private BigDecimal valor;
    private BigInteger registrosAplicados;
    private BigDecimal pagos;
    private BigDecimal retencionesIva;
    private BigDecimal retencionesRenta;
    private BigDecimal notasCredito;
    private BigDecimal saldo;
    private Boolean anulada;

}
