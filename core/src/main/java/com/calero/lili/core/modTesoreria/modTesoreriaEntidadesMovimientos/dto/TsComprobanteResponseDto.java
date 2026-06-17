package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto;

import com.calero.lili.core.enums.EstadoComprobante;
import com.calero.lili.core.enums.TipoComprobante;
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
public class TsComprobanteResponseDto {

    private UUID idComprobante;
    private String sucursal;
    private String tieneComprobante;
    private String numeroComprobante;
    private String fecha;
    private BigDecimal total;
    private String concepto;
    private String observaciones;
    private TipoComprobante tipoComprobante;
    private EstadoComprobante estadoComprobante;
    private UUID idTercero;
    private String tercero;
    private String numeroIdentifiacion;
    private List<DetalleComprobanteResponseDto> detalles;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetalleComprobanteResponseDto {
        private UUID idComprobanteDetalle;
        private String tipoDocumento;
        private String numeroDocumento;
        private String movimiento;
        private String fechaDocumento;
        private TipoComprobante tipoComprobante;
        private String descripcion;
        private BigDecimal valor;
        private UUID idEntidad;
        private String entidad;
    }
}
