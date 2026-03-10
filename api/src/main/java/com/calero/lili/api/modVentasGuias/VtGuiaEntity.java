package com.calero.lili.api.modVentasGuias;

import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.core.dtos.InformacionAdicional;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.api.modAuditoria.Auditable;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vtGuias")
@Builder
@Where(clause = "deleted = false")
public class VtGuiaEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;
    @Column(name = "id_empresa")
    private Long idEmpresa;
    @Column(name = "sucursal", length = 3)
    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idGuia;

    @Column(name = "serie", length = 6)
    private String serie;

    @Column(name = "secuencial", length = 9)
    private String secuencial;
    @Column(name = "numero_autorizacion", length = 49)
    private String numeroAutorizacion;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "fecha_ini_transporte")
    private LocalDate fechaIniTransporte;

    @Column(name = "fecha_fin_transporte")
    private LocalDate fechaFinTransporte;

    private String motivoTraslado;
    private String ruta;
    private String docAduaneroUnico;
    private String codEstabDestino;

    private String codDocSustento;
    private String serieDocSustento;
    private String secuencialDocSustento;
    private String numAutDocSustento;
    private LocalDate fechaEmisionDocSustento;

    @Column(name = "fecha_anulacion")
    private LocalDate fechaAnulacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "formato_documento")
    private FormatoDocumento formatoDocumento;


    @Enumerated(EnumType.STRING)
    private EstadoDocumento estadoDocumento;

    private Integer emailEstado;

    private Integer tipoEmision;
    private Integer ambiente;
    private String fechaAutorizacion;
    private String claveAcceso;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Mensajes> mensajes;

    @Column(name = "comprobante", columnDefinition = "TEXT")
    private String comprobante;

    @Column(name = "numero_items")
    private int numeroItems;

    @Column(name = "anulada")
    private Boolean anulada;

    @Column(name = "impresa")
    private Boolean impresa;


    private String dirPartida;
    private String email;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<InformacionAdicional> informacionAdicional;

    private String concepto;

    @JoinColumn(name = "id_guia", referencedColumnName = "idGuia")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VtGuiaDetalleEntity> detalle = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportista", referencedColumnName = "idTercero")
    private GeTerceroEntity transportista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destinatario", referencedColumnName = "idTercero")
    private GeTerceroEntity destinatario;

    private String codigoDocumento;

}
