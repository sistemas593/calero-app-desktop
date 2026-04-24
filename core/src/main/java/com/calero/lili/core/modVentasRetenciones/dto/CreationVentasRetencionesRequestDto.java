package com.calero.lili.core.modVentasRetenciones.dto;

import com.calero.lili.core.enums.TipoIdentificacion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreationVentasRetencionesRequestDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;

    @NotEmpty(message = "No existe el serie")
    private String serieRetencion;

    @NotEmpty(message = "No existe el secuencial")
    private String secuencialRetencion;

    @NotEmpty(message = "No existe la fecha de emision")
    private String fechaEmisionRetencion;

    //@NotEmpty(message = "No existe el id Cliente")
    private UUID idTercero;

    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;
    private String email;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<ValoresDto> valores;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ValoresDto {
        private String codigo;
        private String codigoPorcentaje;
        private int tarifa;
        private BigDecimal baseImponible;
        private BigDecimal valor;
        private String codigoDocumento;
        private String serie;
        private String secuencial;
        private String fechaEmision;
    }

    private String fechaAnulacion;
    private Boolean anulada;
    private Boolean impresa;

    private String periodoFiscal;
}
