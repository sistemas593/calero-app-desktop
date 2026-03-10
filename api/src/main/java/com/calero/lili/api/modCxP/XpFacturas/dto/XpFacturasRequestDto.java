package com.calero.lili.api.modCxP.XpFacturas.dto;

import com.calero.lili.core.enums.TipoDocumentoFactura;
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
public class XpFacturasRequestDto {

    private String sucursal;
    private String periodo;
    private TipoDocumentoFactura tipoDocumento;
    private String serie;
    private String secuencial;
    private String fechaRegistro;
    private String fechaVencimiento;
    private BigDecimal valor;
    private BigInteger registrosAplicados;
    private BigInteger retencionesAplicadas;
    private BigDecimal pagos;
    private BigDecimal retencionesIva;
    private BigDecimal retencionesRenta;
    private BigDecimal notasCredito;
    private BigDecimal saldo;
    private Boolean anulada;
    private UUID idTercero;
}
