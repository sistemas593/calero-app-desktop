package com.calero.lili.core.modCompras.modComprasLiquidaciones;

import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.dtos.FormasPagoSri;
import com.calero.lili.core.dtos.InformacionAdicional;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.Auditable;
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
@Table(name = "cp_liquidaciones")
@Builder
@Where(clause = "deleted = false")
public class CpLiquidacionesEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;
    @Column(name = "id_empresa")
    private Long idEmpresa;
    @Column(name = "sucursal", length = 3)
    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idLiquidacion;

    @Column(name = "serie", length = 6)
    private String serie;

    @Column(name = "secuencial", length = 9)
    private String secuencial;
    @Column(name = "numero_autorizacion", length = 49)
    private String numeroAutorizacion;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "fecha_anulacion")
    private LocalDate fechaAnulacion;

    @Column(name = "forma_pago", length = 2)
    private String formaPago;
    // (CO) Contado, (CR) Credito

    // info credito
    @Column(name = "dias_credito")
    private Integer diasCredito;
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    @Column(name = "cuotas")
    private Integer cuotas;

    @Enumerated(EnumType.STRING)
    @Column(name = "formato_documento")
    private FormatoDocumento formatoDocumento; // EL DETALLE EN EL OBJETO ENUM

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_documento")
    private EstadoDocumento estadoDocumento; // EL DETALLE EN EL OBJETO ENUM

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

    @Column(name = "anulada")
    private Boolean anulada;

    @Column(name = "impresa")
    private Boolean impresa;

    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;

    private String tipoProveedor;
    private String relacionado;

    private String codDocReembolso;
    private BigDecimal totalComprobantesReembolso;
    private BigDecimal totalBaseImponibleReembolso;
    private BigDecimal totalImpuestoReembolso;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<InformacionAdicional> informacionAdicional;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<FormasPagoSri> formasPagoSri;

    private String concepto;


    @Builder.Default
    @JoinColumn(name = "id_liquidacion", referencedColumnName = "idLiquidacion")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpLiquidacionesValoresEntity> valoresEntity = new ArrayList<>();

    @Builder.Default
    @JoinColumn(name = "id_liquidacion", referencedColumnName = "idLiquidacion")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpLiquidacionesDetalleEntity> detalle = new ArrayList<>();

    @Builder.Default
    @JoinColumn(name = "id_liquidacion", referencedColumnName = "idLiquidacion")
    @OneToMany(cascade = CascadeType.ALL)
    private List<CpLiquidacionesReembolsosEntity> reembolsosEntity = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private GeTerceroEntity proveedor;


}
