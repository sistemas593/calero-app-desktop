package com.calero.lili.core.modVentas.notasCredito.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.FormaPago;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.Liquidar;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoIngreso;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreationNotaCreditoRequestDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;
    @NotNull(message = "El formato documento no puede ser nulo")
    private FormatoDocumento formatoDocumento;

    @NotEmpty(message = "No existe la serie")
    private String serie;
    @NotEmpty(message = "No existe el secuencial")
    private String secuencial;

    private String numeroAutorizacion;

    @NotEmpty(message = "No existe la fecha de emision")
    private String fechaEmision;

    @NotEmpty(message = "No existe el codigo documento")
    private String codigoDocumento;

    //@NotEmpty(message = "No existe el tipo")
    private TipoIngreso tipoIngreso;

    private Liquidar liquidar;

    @NotNull(message = "No existe el nombre del cliente")
    private UUID idTercero;

    // TODO QUITAR NOMBRE DEL CLIENTE
    @NotBlank(message = "No existe el nombre del cliente")
    private String terceroNombre;
    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;
    private String tipoCliente;
    private String relacionado;


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
    private FormaPago formaPago;
    private Integer diasCredito;
    private Integer cuotas;
    private Integer czona;
    private String documentoElectronico;
    private Integer idVendedor;
    private Boolean impresa;

    private List<InformacionAdicionalDto> informacionAdicional;


    @NotEmpty(message = "No existe el motivo de la nota de credito")
    private String concepto;

    @NotEmpty(message = "No existe el codigo documento la que aplica la nota de credito")
    private String modCodigoDocumento;

    @NotEmpty(message = "No existe la serie a la que aplica la nota de credito")
    private String modSerie;

    @NotEmpty(message = "No existe el secuencial a la que aplica la nota de credito")
    private String modSecuencial;

    @NotEmpty(message = "No existe la fecha del documento a la que aplica la nota de credito")
    private String modFechaEmision;

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

    private Integer ambiente;
}
