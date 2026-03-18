package com.calero.lili.core.modVentasRetenciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetDto {

    private UUID idRetencion;
    private String sucursal;
    private String fechaEmision;
    private String serie;
    private String secuencial;

    private Boolean anulada;

    private List<ResponseValoresDto> valores;

    private String fechaAutorizacion;
    private String claveAcceso;
    private TerceroDto tercero;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TerceroDto {
        private UUID idTercero;
        private String tercero;
        private String tipoIdentificacion;
        private String numeroIdentificacion;
    }
}
