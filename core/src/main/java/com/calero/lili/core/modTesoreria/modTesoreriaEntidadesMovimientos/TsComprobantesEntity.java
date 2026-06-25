package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.enums.EstadoComprobante;
import com.calero.lili.core.enums.TipoComprobante;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.TsCajasEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.math.BigDecimal;
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
@Table(name = "ts_comprobantes")
@Where(clause = "deleted = false")
public class TsComprobantesEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idComprobante;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    private String sucursal;

    @Column(name = "tiene_comprobante")
    private String tieneComprobante; // S o N

    @Column(name = "numero_comprobante", unique = true)
    private String numeroComprobante;

    private Integer anio;

    private LocalDate fecha;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "concepto")
    private String concepto;

    @Column(name = "observaciones")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    private TipoComprobante tipoComprobante;

    @Enumerated(EnumType.STRING)
    private EstadoComprobante estadoComprobante;

    @Builder.Default
    @JoinColumn(name = "id_comprobante", referencedColumnName = "idComprobante")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TsComprobanteDetallesEntity> detalle = new ArrayList<>();


    @ManyToOne()
    @JoinColumn(name = "idTercero", referencedColumnName = "idTercero")
    private GeTerceroEntity tercero;

    @ManyToOne()
    @JoinColumn(name = "idCaja", referencedColumnName = "idCaja")
    private TsCajasEntity caja;


}
