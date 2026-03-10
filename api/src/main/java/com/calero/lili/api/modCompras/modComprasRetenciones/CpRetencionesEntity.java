package com.calero.lili.api.modCompras.modComprasRetenciones;

import com.calero.lili.core.dtos.InformacionAdicional;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.api.modAuditoria.Auditable;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
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
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cpRetenciones")
@Builder
@Where(clause = "deleted = false")
public class CpRetencionesEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;
    @Column(name = "id_empresa")
    private Long idEmpresa;
    @Column(name = "sucursal", length = 3)
    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idRetencion;

    @Column(name = "serie_retencion", length = 6)
    private String serieRetencion;
    @Column(name = "secuencial_retencion", length = 9)
    private String secuencialRetencion;
    @Column(name = "numero_autorizacion_retencion", length = 49)
    private String numeroAutorizacionRetencion;

    @Column(name = "fecha_emision_retencion")
    private LocalDate fechaEmisionRetencion;

    @Column(name = "fecha_anulacion")
    private LocalDate fechaAnulacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "formato_documento")
    private FormatoDocumento formatoDocumento;

    @Column(name = "estado_documento")
    @Enumerated(EnumType.STRING)
    private EstadoDocumento estadoDocumento;

    private Integer emailEstado;
    private String email;

    private Integer tipoEmision;


    private Integer ambiente;

    private String fechaAutorizacion;

    private String claveAcceso;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Mensajes> mensajes;

    @Column(name = "comprobante", columnDefinition = "TEXT")
    private String comprobante;

    private BigDecimal total;

    @Column(name = "anulada")
    private Boolean anulada;

    @Column(name = "impresa")
    private Boolean impresa;

    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<InformacionAdicional> informacionAdicional;

    private String concepto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private GeTerceroEntity proveedor;

    private String codigoDocumento;

    private String relacionado;

    private String periodoFiscal;

    @JoinColumn(name = "idRetencion", referencedColumnName = "idRetencion")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpRetencionReferencias> referencias = new ArrayList<>();


}
