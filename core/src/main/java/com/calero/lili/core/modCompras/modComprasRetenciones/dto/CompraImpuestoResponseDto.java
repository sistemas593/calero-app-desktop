package com.calero.lili.core.modCompras.modComprasRetenciones.dto;

import com.calero.lili.core.enums.SustentoCodigos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraImpuestoResponseDto {

    // TODO TRAER EL SUBTOTAL E IMPUESTOS, BASES IMPONIBLES = SUMA DE SUBTOTAL, IMPUESTOS = VALOR

    private UUID idImpuestos;
    private String serie;
    private String secuencial;
    private String numeroAutorizacion;
    private String fechaEmision;
    private String codigoDocumento;
    private String documento;
    private SustentoCodigos codigoSustento;
    private String sustento;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
}
