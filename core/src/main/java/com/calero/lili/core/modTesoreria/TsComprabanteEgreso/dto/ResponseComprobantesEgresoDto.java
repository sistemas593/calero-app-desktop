package com.calero.lili.core.modTesoreria.TsComprabanteEgreso.dto;

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
public class ResponseComprobantesEgresoDto {

    private String sucursal;

    private Boolean fisico;

    private String codigoSerie;

    private String numeroComprobanteEgreso;

    private String numeroIdentificacion;

    private String fechaComprobante;

    private BigDecimal valor;

    private String concepto;

    private String nombre;

    private String observaciones;

    private TerceroDto tercero;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TerceroDto {
        private UUID idTercero;
        private String cliente;
    }

}
