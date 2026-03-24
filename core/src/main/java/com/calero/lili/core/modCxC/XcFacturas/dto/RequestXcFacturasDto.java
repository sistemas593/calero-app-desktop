package com.calero.lili.core.modCxC.XcFacturas.dto;

import com.calero.lili.core.enums.TipoDocumentoFactura;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Data
public class RequestXcFacturasDto {

    private String sucursal;
    private String periodo;

    @NotNull(message = "No existe tipo documento")
    private TipoDocumentoFactura tipoDocumento;


    @NotEmpty(message = "No existe serie")
    private String serie;


    @NotEmpty(message = "No existe secuencial")
    private String secuencial;


    @NotEmpty(message = "No existe fecha emisión")
    private String fechaEmision;

    private String fechaVencimiento;

    @NotNull(message = "No existe valor")
    private BigDecimal valor;

    private BigInteger registrosAplicados;
    private BigDecimal pagos;
    private BigDecimal retencionesIva;
    private BigDecimal retencionesRenta;
    private BigDecimal notasCredito;

    @NotNull(message = "No existe saldo")
    private BigDecimal saldo;
    private Boolean anulada;
    private UUID idTercero;

}
