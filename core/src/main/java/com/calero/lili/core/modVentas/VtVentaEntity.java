package com.calero.lili.core.modVentas;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.dtos.FormasPagoSri;
import com.calero.lili.core.dtos.InformacionAdicional;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.enums.ComercioExterior;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormaPago;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosEntity;
import com.calero.lili.core.modVentasVendedores.VtVendedorEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vtVentas")
@Builder
@Where(clause = "deleted = false")
public class VtVentaEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;
    @Column(name = "id_empresa")
    private Long idEmpresa;
    @Column(name = "sucursal", length = 3)
    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idVenta;

    @Column(name = "tipo_venta")
    private String tipoVenta; // EL DETALLE EN EL OBJETO ENUM

    @Column(name = "serie", length = 6)
    private String serie;

    @Column(name = "secuencial", length = 9)
    private String secuencial;
    @Column(name = "numero_autorizacion", length = 49)
    private String numeroAutorizacion;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    //    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ingreso")
    private String tipoIngreso;

    @Column(name = "codigo_documento")
    private String codigoDocumento;
    // 01 Factura 02 Nota de venta

//    @Column(name = "tipo_doc")
//    private String tipoDoc;

    @Column(name = "liquidar", length = 1)
    private String liquidar;
    // A Liquidar el mes Actual, S Liquidar el mes Siguiente

    @Column(name = "fecha_anulacion")
    private LocalDate fechaAnulacion;

    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;

    // info credito
    @Column(name = "dias_credito")
    private Integer diasCredito;
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    @Column(name = "cuotas")
    private Integer cuotas;


    @Column(name = "guia_remision_serie", length = 6)
    private String guiaRemisionSerie;

    @Column(name = "guia_remision_secuencial", length = 9)
    private String guiaRemisionSecuencial;

    @Enumerated(EnumType.STRING)
    @Column(name = "formato_documento")
    private FormatoDocumento formatoDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_documento")
    private EstadoDocumento estadoDocumento;

    private Integer emailEstado;
    private String email;

    private Integer tipoEmision;


    private Integer ambiente;
    private LocalDateTime fechaAutorizacion;
    private String claveAcceso;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Mensajes> mensajes;

    @Column(name = "comprobante", columnDefinition = "TEXT")
    private String comprobante;

    @Column(name = "numero_items")
    private int numeroItems;

    private BigDecimal subtotal;
    private BigDecimal totalDescuento;
    private BigDecimal total;
    private BigDecimal totalImpuesto;

    @Column(name = "id_zona")
    private UUID idZona;

    @Column(name = "anulada")
    private Boolean anulada;

    @Column(name = "impresa")
    private Boolean impresa;

    private String relacionado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<InformacionAdicional> informacionAdicional;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<FormasPagoSri> formasPagoSri;

    private String concepto;

    private String modCodigoDocumento;
    private String modSerie;
    private String modSecuencial;
    private LocalDate modFechaEmision;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
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

    @Builder.Default
    @JoinColumn(name = "id_venta", referencedColumnName = "idVenta")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VtVentaValoresEntity> valoresEntity = new ArrayList<>();

    @Builder.Default
    @JoinColumn(name = "id_venta", referencedColumnName = "idVenta")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VtVentaDetalleEntity> detalle = new ArrayList<>();

    @Builder.Default
    @JoinColumn(name = "id_venta", referencedColumnName = "idVenta")
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<VtVentaReembolsosEntity> reembolsosEntity = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor")
    private VtVendedorEntity vendedor;

    private UUID idAsiento;

    // al traer las facturas anuladas los valores deben ser cero, total, subtotal.

}
