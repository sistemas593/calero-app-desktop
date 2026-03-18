package com.calero.lili.core.modVentas.facturas.dto;

import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.ComercioExterior;
import com.calero.lili.core.enums.FormaPago;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoTerceroPerSoc;
import com.calero.lili.core.modVentas.facturas.dto.detalles.DetalleGetDto;
import jakarta.persistence.Column;
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
public class GetFacturaDto {

    private UUID idVenta;
    private String sucursal;
    private String tipoVenta;
    private String fechaEmision;
    private String serie;
    private String secuencial;

    private String codigoDocumento;
    private String tipoIngreso;
    private String liquidar;

    private String guiaRemisionSerie;
    private String guiaRemisionSecuencial;

    private UUID idTercero;

    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String terceroNombre;
    private String tipoCliente;
    private String relacionado;
    private String email;

    private BigDecimal subtotal;
    private BigDecimal totalDescuento;
    private BigDecimal total;

    private Boolean anulada;

    private List<ResponseValoresDto> valores;

    private Integer numeroItems;

    private String fechaVencimiento;
    private String fechaAnulacion;
    private FormaPago formaPago;
    private Integer diasCredito;
    private Integer cuotas;

    private List<DetalleGetDto> detalle;

    private List<InformacionAdicionalDto> informacionAdicional;
    private List<FormasPagoDto> formasPagoSri;

    private String fechaAutorizacion;
    private String claveAcceso;

    private String motivo;

    private Exportacion exportacion;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Exportacion {
        private ComercioExterior comercioExterior;
        private String incoTermFactura;
        private String lugarIncoTerm;
        private String paisOrigen;
        private String nombrePaisOrigen;
        private String puertoEmbarque;
        private String puertoDestino;
        private String paisDestino;
        private String nombrePaisDestino;
        private String paisAdquisicion;
        private String nombrePaisAdquisicion;
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
    @Builder
    public static class Reembolso {
        private UUID idReembolso;
        private String tipoIdentificacionReemb;
        private String numeroIdentificacionReemb;
        private String codPaisPagoReemb;
        private TipoTerceroPerSoc tipoProveedorReemb;
        private String codigoDocumentoReemb;
        private String serieReemb;
        private String secuencialReemb;
        private String fechaEmisionReemb;
        private String numeroAutorizacionReemb;
        private List<Valores> reembolsosValores;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Valores {
        private String codigo;
        private String codigoPorcentaje;
        private BigDecimal tarifa;
        private BigDecimal baseImponible;
        private BigDecimal valor;
    }


    private SustitutivaGuiaRemision sustitutivaGuiaRemision;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SustitutivaGuiaRemision {
        private UUID idTransportista;
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
        private List<Destino> destinos;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Destino {
        private String motivoTraslado;
        private String ruta;
        private String docAduaneroUnico;
        private String codEstabDestino;
    }


    private Integer ambiente;
    private FormatoDocumento formatoDocumento;
    private String numeroAutorizacion;
    private BigDecimal totalImpuesto;
}
