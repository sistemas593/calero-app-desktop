package com.calero.lili.api.modVentasPedidos.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.TipoIdentificacion;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreationComprasPedidosRequestDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;

    @NotEmpty(message = "No existe el secuencial")
    private String secuencial;

    @NotEmpty(message = "No existe la fecha de emision")
    private String fechaEmision;

    //@NotEmpty(message = "No existe el id Cliente")
    private UUID idTercero;

    @NotEmpty(message = "No existe el nombre")
    private String terceroNombre;
    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;
    private String email;

    private TerceroDto terceroDatos; // no es una lista es un solo objeto
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TerceroDto {
        private String telefonos;
        private String ciudad;
        private String direccion;
    }

    private String concepto;

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
    }

    @NotNull(message = "No existe el subtotal")
    private BigDecimal subtotal;

    @NotNull(message = "No existe el total descuento")
    private BigDecimal totalDescuento;

    private BigDecimal total;

    private Integer numeroItems;
    private String fechaVencimiento;
    private String fechaAnulacion;
    private String formaPago;
    private Integer diasCredito;
    private Integer czona;
    private String emailEstado;
    private Integer idVendedor;
    private Boolean anulada;
    private Boolean impresa;

    private List<InformacionAdicionalDto> informacionAdicional;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<DetailDto> detalle;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
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
        @Builder
        public static class DetalleAdicional {
            private String nombre;
            @Column(length = 300)
            private String valor;
        }
    }

}
