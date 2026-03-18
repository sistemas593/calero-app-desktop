package com.calero.lili.core.modComprasOrden.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrdenCompraRequestDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;

    @NotEmpty(message = "No existe el secuencial")
    private String secuencial;

    @NotEmpty(message = "No existe la fecha de emision")
    private String fechaEmision;

    private UUID idTercero;

    private String concepto;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<ValoresDto> valores;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValoresDto {
        private String codigo;
        private String codigoPorcentaje;
        private BigDecimal tarifa;
        private BigDecimal baseImponible;
        private BigDecimal valor;
    }

    @NotNull(message = "No existe el subtotal")
    private BigDecimal subtotal;

    @NotNull(message = "No existe el total descuento")
    private BigDecimal totalDescuento;

    private BigDecimal total;
    private Integer numeroItems;
    private String fechaVencimiento;
    private String formaPago;
    private Integer diasCredito;
    private Integer cuotas;
    private Boolean impresa;

    private List<InformacionAdicionalDto> informacionAdicional;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<DetailDto> detalle;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailDto {
        private UUID idItem;
        private int itemOrden;
        @NotEmpty(message = "No existe el codigo principal")
        private String codigoPrincipal;
        private String codigoAuxiliar;
        private String codigoBarras;
        private String descripcion;
        private String unidadMedida;
        private BigDecimal precioUnitario;
        private BigDecimal cantidad;
        private BigDecimal dsctoItem;
        private BigDecimal descuento;
        private BigDecimal subtotalItem;

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

        private List<DetalleAdicional> detAdicional;
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class DetalleAdicional {
            private String nombre;
            @Column(length = 300)
            private String valor;
        }
    }
}
