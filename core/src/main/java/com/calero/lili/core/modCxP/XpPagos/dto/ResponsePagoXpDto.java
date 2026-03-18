package com.calero.lili.core.modCxP.XpPagos.dto;

import com.calero.lili.core.enums.TipoDocumentoPago;
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
public class ResponsePagoXpDto {

    private UUID idPago;
    private UUID idPagoGrupo;
    private String sucursal;
    private TipoDocumentoPago tipoDocumento;
    private String fechaPago;
    private String concepto;
    private BigDecimal valor;
    private Boolean anulada;

}
