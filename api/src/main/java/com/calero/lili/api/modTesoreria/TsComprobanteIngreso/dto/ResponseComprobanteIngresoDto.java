package com.calero.lili.api.modTesoreria.TsComprobanteIngreso.dto;

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
public class ResponseComprobanteIngresoDto {
    private String sucursal;
    private String codigoSerie;
    private String numeroComprobante;
    private String numeroIdentificacion;
    private String fechaComprobante;
    private BigDecimal valor;
    private String concepto;
    private String nombre;
    private String observaciones;
    private Boolean fisico;

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
