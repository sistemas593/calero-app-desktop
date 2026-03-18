package com.calero.lili.core.modContabilidad.modAsientos;

import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.enums.TipoAsiento;
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
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cnAsientos")
@Builder
@Where(clause = "deleted = false")
public class CnAsientosEntity extends Auditable implements Persistable<UUID> {

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "sucursal")
    private String sucursal;

    @Column(name = "id_periodo")
    private String idPeriodo;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idAsiento;

    @Column(name = "tipo_asiento")
    @Enumerated(EnumType.STRING)
    private TipoAsiento tipoAsiento;

    @Column(name = "numero_asiento")
    private String numeroAsiento;

    @Column(name = "fecha_asiento")
    private LocalDate fechaAsiento;

    @Column(name = "concepto")
    private String concepto;

    @Column(name = "mayorizado")
    private Boolean mayorizado;

    @Column(name = "anulada")
    private Boolean anulada;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "codigo_serie")
    private String codigoSerie;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "observaciones")
    private String observaciones;

    @JoinColumn(name = "idAsiento", referencedColumnName = "idAsiento")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CnAsientosDetalleEntity> detalleEntity = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTercero")
    private GeTerceroEntity tercero;

    @Transient
    @Builder.Default
    private boolean isNewEntity = true;

    @Override
    public UUID getId() {
        return idAsiento;
    }

    @Override
    public boolean isNew() {
        return isNewEntity;
    }

}
