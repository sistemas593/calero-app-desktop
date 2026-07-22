package com.calero.lili.core.modVentas.notasDebito.dto;

import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.dtos.ValoresDto;
import com.calero.lili.core.enums.ComercioExterior;
import com.calero.lili.core.enums.FormaPago;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.Liquidar;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoIngreso;
import com.calero.lili.core.dtos.DetallesDto;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreationNotaDebitoRequestDto {

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

    @NotEmpty(message = "No existe el codigo documento")
    private String codigoDocumento;

    //@NotEmpty(message = "No existe el tipo")
    private TipoIngreso tipoIngreso;

    private Liquidar liquidar;

    private String guiaRemisionSerie;
    private String guiaRemisionSecuencial;

    @NotNull(message = "No existe el id del cliente")
    private UUID idTercero;

    @NotBlank(message = "No existe el nombre del cliente")
    private String terceroNombre;
    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;
    private String email;
    private String tipoCliente;
    private String relacionado;
    private String concepto;

    private List<ValoresDto> valores;

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
    @Valid
    private List<FormasPagoDto> formasPagoSri;

    @NotEmpty(message = "No existe el codigo documento a la que aplica la nota de debito")
    private String modCodigoDocumento;

    @NotEmpty(message = "No existe la serie a la que aplica la nota de debito")
    private String modSerie;

    @NotEmpty(message = "No existe el secuencial a la que aplica la nota de debito")
    private String modSecuencial;

    @NotEmpty(message = "No existe la fecha del documento a la que aplica la nota de debito")
    private String modFechaEmision;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<DetallesDto> detalle;


    private Exportacion exportacion;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Exportacion {
        private ComercioExterior comercioExterior;
        private String incoTermFactura;
        private String lugarIncoTerm;
        private String paisOrigen;
        private String puertoEmbarque;
        private String puertoDestino;
        private String paisDestino;
        private String paisAdquisicion;
        private String incoTermTotalSinImpuestos;
    }

    private BigDecimal fleteInternacional;
    private BigDecimal seguroInternacional;
    private BigDecimal gastosAduaneros;
    private BigDecimal gastosTransporteOtros;

    private List<Reembolso> reembolsos;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Reembolso {
        private String tipoIdentificacionReemb;
        private String numeroIdentificacionReemb;
        private String codPaisPagoReemb;
        private String tipoProveedorReemb;
        private String codigoDocumentoReemb;
        private String serieReemb;
        private String secuencialReemb;
        private String fechaEmisionReemb;
        private String numeroAutorizacionReemb;

        @Valid
        @NotEmpty(message = "No existen valores en el reembolso")
        private List<ValoresDto> reembolsosValores;
//        @Data
//        @AllArgsConstructor
//        @NoArgsConstructor
//        public static class ValoresDto {
//            private String codigo;
//            private String codigoPorcentaje;
//            private int tarifa;
//            private BigDecimal baseImponible;
//            private BigDecimal valor;
//        }

    }

    private SustitutivaGuiaRemision sustitutivaGuiaRemision;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SustitutivaGuiaRemision {
        private String dirPartida;
        private String dirDestinatario;
        private String fechaIniTransporte;
        private String fechaFinTransporte;
        private String razonSocialTransportista;
        private String tipoIdentificacionTransportista;
        private String numeroIdentificacionTransportista;
        private String placa;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(columnDefinition = "jsonb")
        private List<Destino> destinos; // ABAJO DEFINO LA LISTA CLIENTEDATOS
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Destino {
        private String motivoTraslado;
        private String ruta;
        private String docAduaneroUnico;
        private String codEstabDestino;
    }

    private Integer ambiente;
}
