package com.calero.lili.api.modContabilidad.modAsientos;

import com.calero.lili.api.modComprasItems.GeItemEntity;
import com.calero.lili.api.modContabilidad.modCentroCostos.CnCentroCostosEntity;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cnAsientosDetalle")
@Builder
public class CnAsientosDetalleEntity implements Persistable<UUID> {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idAsientoDetalle;

    @Column(name = "item_orden")
    private Integer itemOrden;

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "fecha_documento")
    private LocalDate fechaDocumento;

    @Column(name = "debe")
    private BigDecimal debe;

    @Column(name = "haber")
    private BigDecimal haber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idItem")
    private GeItemEntity geItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTercero")
    private GeTerceroEntity tercero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCuenta")
    private CnPlanCuentaEntity cuenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCentroCostos")
    private CnCentroCostosEntity centroCostos;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Transient
    @Builder.Default
    private boolean isNewEntity = true;

    @Override
    public UUID getId() {
        return idAsientoDetalle;
    }

    @Override
    public boolean isNew() {
        return isNewEntity;
    }

}
