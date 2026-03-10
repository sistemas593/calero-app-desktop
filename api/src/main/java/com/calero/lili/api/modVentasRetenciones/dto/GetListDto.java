package com.calero.lili.api.modVentasRetenciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@Builder
public class GetListDto {

    private String sucursal;
    private UUID idRetencion;

    private String serie;
    private String secuencial;

    private String fechaEmision;

    private Boolean anulada;

    private List<ResponseValoresDto> valores;

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
