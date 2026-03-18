package com.calero.lili.core.modVentas.notasDebito.dto.detalles;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DetalleGetDto {


    private UUID idItem;
    private Integer itemOrden;
    private String codigoPrincipal;
    private String codigoAuxiliar;
    private String codigoBarras;
    private String descripcion;
    private BigDecimal precioUnitario;
    private BigDecimal cantidad;
    private BigDecimal dsctoItem;
    private BigDecimal descuento;
    private BigDecimal subTotalItem;
    private String unidadMedida;

    private List<DetalleAdicional> detAdicional;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetalleAdicional {
        private String nombre;
        @Column(length = 300)
        private String valor;
    }

    private List<Impuestos> impuesto;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Impuestos {
        private String codigo;
        private String codigoPorcentaje;
        private BigDecimal tarifa;
        private BigDecimal baseImponible;
        private BigDecimal valor;
    }

}
