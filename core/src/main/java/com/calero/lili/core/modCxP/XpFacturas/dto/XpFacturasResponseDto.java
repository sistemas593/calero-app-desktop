package com.calero.lili.core.modCxP.XpFacturas.dto;

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
public class XpFacturasResponseDto {

    private UUID idFactura;
    private String sucursal;
    private String periodo;
    private String tipoDocumento;
    private String serie;
    private String secuencial;
    private String fechaRegistro;
    private String fechaVencimiento;
    private BigDecimal valor;
    private BigInteger registrosAplicados;
    private BigDecimal pagos;
    private BigDecimal retencionesIva;
    private BigDecimal retencionesRenta;
    private BigDecimal notasCredito;
    private BigDecimal saldo;
    private Boolean anulada;

    private Proveedor proveedor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Proveedor {
        private UUID idTercero;
        private String proveedor;
    }
}
