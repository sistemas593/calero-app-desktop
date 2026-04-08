package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.dtos.FormasPagoSri;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.Auditable;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.PagoExterior;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.tablas.tbDocumentos.TbDocumentoEntity;
import com.calero.lili.core.tablas.tbSustentos.TbSustentosEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cp_impuestos")
@Where(clause = "deleted = false")
public class CpImpuestosEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;
    @Column(name = "id_empresa")
    private Long idEmpresa;

    private String sucursal;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idImpuestos;

    @ManyToOne()
    @JoinColumn(name = "codigoDocumento", referencedColumnName = "codigoDocumento")
    private TbDocumentoEntity documento;

    @Column(name = "serie")
    private String serie;

    @Column(name = "secuencial")
    private String secuencial;

    @Column(name = "numero_autorizacion")
    private String numeroAutorizacion;

    @Column(name = "fecha_autorizacion")
    private LocalDate fechaAutorizacion;

    @NotNull
    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    private String tipoIdentificacion;

    @Column(name = "numero_identificacion")
    private String numeroIdentificacion;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Mensajes> mensajes;

    @Column(name = "comprobante", columnDefinition = "TEXT")
    @JsonIgnore
    private String comprobante;

    @Column(name = "destino")
    private String destino;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @ManyToOne()
    @JoinColumn(name = "codigoSustento", referencedColumnName = "codigoSustento")
    private TbSustentosEntity sustento;

    @Column(name = "tipo_contribuyente")
    private String tipoContribuyente;

    @Column(name = "concepto", length = 200)
    private String concepto;

    @Column(name = "tipo_proveedor", length = 2)
    private String tipoProveedor;

    private String relacionado;

    @Column(name = "referencia", length = 100)
    private String referencia;

    @Column(name = "liquidar", length = 1)
    private String liquidar;

    @Column(name = "devolucionIva", length = 1)
    private String devolucionIva;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    private String pagoLocExt; // si es 01, no vendria el objeto y si es 02 deberia a ver objeto, igual en el dto

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private PagoExterior pagoExterior; // Se cambio a una clase sola

    @Builder.Default
    @JoinColumn(name = "id_impuestos", referencedColumnName = "idImpuestos")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpImpuestosValoresEntity> valoresEntity = new ArrayList<>(); // IMPUESTOS DOC SUSTENTO

    @Builder.Default
    @JoinColumn(name = "idImpuestos", referencedColumnName = "idImpuestos")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpImpuestosCodigosEntity> codigosEntity = new ArrayList<>(); // RETENCIONES

    @Builder.Default
    @JoinColumn(name = "id_impuestos", referencedColumnName = "idImpuestos")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpImpuestosReembolsosEntity> reembolsosEntity = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private GeTerceroEntity tercero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRetencion")
    private CpRetencionesEntity retencion;


    @Column(name = "id_parent")
    private UUID idParent;

    private String origen;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<FormasPagoSri> formasPagoSri;
}
