package com.calero.lili.core.modVentas.facturas.dto;

import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.dtos.ValoresDto;
import com.calero.lili.core.enums.ComercioExterior;
import com.calero.lili.core.enums.FormaPago;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.Liquidar;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoIngreso;
import com.calero.lili.core.modVentas.dto.DetailDto;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
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
public class CreationFacturaRequestDto {

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

    @NotNull(message = "No existe el tipo de ingreso")
    private TipoIngreso tipoIngreso;

    @NotNull(message = "No existe Liquidar")
    private Liquidar liquidar;


    private String guiaRemisionSerie;
    private String guiaRemisionSecuencial;

    @NotNull(message = "No existe el id Tercero")
    private UUID idTercero;

    @NotNull(message = "No existe el tipo identificacion")
    private TipoIdentificacion tipoIdentificacion;

    private String email;
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

    private Integer idVendedor;
    private Boolean impresa;

    private List<InformacionAdicionalDto> informacionAdicional;
    private List<FormasPagoDto> formasPagoSri;

    private String motivo;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<DetailDto> detalle;



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

    private SustitutivaGuiaRemision sustitutivaGuiaRemision;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SustitutivaGuiaRemision {

        private UUID idTransportista;
        private String dirPartida;
        private String dirDestinatario;
        private String fechaIniTransporte;
        private String fechaFinTransporte;
        @JdbcTypeCode(SqlTypes.JSON)
        @Column(columnDefinition = "jsonb")
        private List<Destino> destinos;
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
    private BigDecimal totalImpuesto;

    private List<UUID> listIdReembolsos;

    @NotNull(message = "Cuentas por cobras no existe")
    private Boolean cuentaPorCobrar;

}
