package com.calero.lili.core.modCompras.modComprasLiquidaciones.dto;

import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.detalles.ValoresDto;
import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.FormatoDocumento;
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
public class CreationRequestLiquidacionCompraDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;
    @NotNull(message = "No existe el formato del documento")
    private FormatoDocumento formatoDocumento;

    @NotEmpty(message = "No existe la serie")
    private String serie;
    @NotEmpty(message = "No existe el secuencial")
    private String secuencial;

    private String numeroAutorizacion;

    @NotEmpty(message = "No existe la fecha de emision")
    private String fechaEmision;

    private UUID idTercero;

    private String terceroNombre;
    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;
    private String email;
    private String tipoProveedor;
    private String relacionado;

    private String concepto;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<ValoresDto> valores;

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
    private Integer czona;
    private String documentoElectronico;
    private String emailEstado;
    private Integer idVendedor;
    private Boolean impresa;

    private List<InformacionAdicionalDto> informacionAdicional;
    private List<FormasPagoDto> formasPagoSri;

    private String motivo;

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

    private String codDocReembolso;
    private BigDecimal totalComprobantesReembolso;
    private BigDecimal totalBaseImponibleReembolso;
    private BigDecimal totalImpuestoReembolso;

    private List<UUID> listIdLiquidacionesReembolso;

    private List<CreationCompraImpuestoRequestDto> compraImpuestos;


    private Integer ambiente;

}
