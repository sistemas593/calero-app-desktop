package com.calero.lili.api.modCxC.XcRetenciones.dto;

import com.calero.lili.core.enums.TipoDocumentoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XcRetencionesDto {

    private String sucursal;
    private TipoDocumentoPago tipoDocumento;
    private String fechaPago;
    private String concepto;
    private BigDecimal valor;
    private Boolean anulada;

}
