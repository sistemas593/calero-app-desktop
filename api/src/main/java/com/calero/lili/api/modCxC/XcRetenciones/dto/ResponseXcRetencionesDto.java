package com.calero.lili.api.modCxC.XcRetenciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseXcRetencionesDto {

    private UUID idPago;
    private UUID idPagoGrupo;
    private String sucursal;
    private String tipoDocumento;
    private String fechaPago;
    private String concepto;
    private BigDecimal valor;
    private Boolean anulada;

}
