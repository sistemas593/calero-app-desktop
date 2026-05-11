package com.calero.lili.core.modVentasRetenciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetListDto {

    private UUID idRetencion;
    private String sucursal;
    private String numeroAutorizacionRetencion;
    private String serieRetencion;
    private String secuencialRetencion;
    private String fechaEmisionRetencion;
    private Boolean anulada;
    private List<ResponseValoresDto> valores;
    private TerceroDto tercero;
    private Boolean existeComprobante;

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
