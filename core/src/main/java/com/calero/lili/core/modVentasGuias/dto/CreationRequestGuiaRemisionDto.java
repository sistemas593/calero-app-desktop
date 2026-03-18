package com.calero.lili.core.modVentasGuias.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.FormatoDocumento;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
public class CreationRequestGuiaRemisionDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;

    @NotNull(message = "No existe el formato del documento")
    private FormatoDocumento formatoDocumento;

    @NotEmpty(message = "No existe la serie")
    private String serie;

    @NotEmpty(message = "No existe el secuencial")
    private String secuencial;

    private String numeroAutorizacion;

    @NotEmpty(message = "No existe la fecha emision")
    private String fechaEmision;

    @NotEmpty(message = "No existe la fecha inicio transporte")
    private String fechaIniTransporte;

    @NotEmpty(message = "No existe la fecha fin transporte")
    private String fechaFinTransporte;

    @NotBlank(message = "No existe direccion partida")
    private String dirPartida;


    @NotBlank(message = "No existe motivo del traslado")
    private String motivoTraslado;

    private String docAduaneroUnico;

    private String codEstabDestino;

    @NotBlank(message = "No existe la ruta")
    private String ruta;

    private DocumentoSustentoDto documentoSustento;

    private String codDocSustento;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentoSustentoDto {

        private String serieDocSustento;
        private String secuencialDocSustento;
        private String numAutDocSustento;
        private String fechaEmisionDocSustento;
    }


    private Integer numeroItems;
    private Boolean impresa;

    private List<InformacionAdicionalDto> informacionAdicional;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<DetailGuiaRemisionDto> detalle;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailGuiaRemisionDto {
        private UUID idItem;
        private int itemOrden;
        @NotBlank(message = "No existe el codigo principal en el item")
        private String codigoPrincipal;
        private String codigoAuxiliar;
        private String codigoBarras;
        @NotBlank(message = "No existe la descripcion en el item")
        private String descripcion;
        private String unidadMedida;
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor que 0")
        private BigDecimal cantidad;

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

    private String codigoDocumento;
    private Integer ambiente;

    @NotNull(message = "Es requerido el destinario")
    private UUID idDestinatario;

    @NotNull(message = "Es requerido el transportista")
    private UUID idTransportista;
}
