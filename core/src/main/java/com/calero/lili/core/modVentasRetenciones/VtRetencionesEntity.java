package com.calero.lili.core.modVentasRetenciones;

import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.Auditable;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
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
import org.hibernate.annotations.Where;

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
@Table(name = "vtRetenciones")
@Builder
@Where(clause = "deleted = false")
public class VtRetencionesEntity extends Auditable {

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
    @Column(name = "numero_autorizacion", length = 49)
    private String numeroAutorizacionRetencion;

    @Column(name = "fecha_emision_retencion")
    private LocalDate fechaEmisionRetencion;


    @Enumerated(EnumType.STRING)
    @Column(name = "formato_documento")
    private FormatoDocumento formatoDocumento; // EL DETALLE EN EL OBJETO ENUM

    private LocalDateTime fechaAutorizacion;

    private String claveAcceso;

    @Column(name = "comprobante", columnDefinition = "TEXT")
    private String comprobante;

    private LocalDate periodoFiscal;

    // VALORES no ponia aqui porque si quiero manejar ice o cualquier otro impuesto toca aumentar campos
    // PARA COMPRAS IGUAL PUEDE EXISTIR ICE U OTROS IMPUESTOS, CAMBIAR EL % DE IVA O APARECER OTRO
    // igual si quiero internacionalizar la aplicacion
    // PARA EXPORTAR A EXCEL IGUAL TOCA RECORRER LA TABLA PARA CAMBIAR LA FECHA ASI ESTE EN UNA SOLA TABLA, SOLO TOCA RECORRER TAMBIEN DE VALORES
    // SI APARECE UN NUEVO % DE IVA TOCA CAMBIAR BESTIALMENTE EN TODE EL SISTEMA, COMPRAS, IMPUESTOUES, REEMBOLSOS PARA CREAR NUEVOS CAMPOS


    @Builder.Default
    @JoinColumn(name = "id_retencion", referencedColumnName = "idRetencion")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VtRetencionesValoresEntity> valoresEntity = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private GeTerceroEntity cliente;



}
