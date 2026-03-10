package com.calero.lili.api.modRRHH.modRRHHCabecera;

import com.calero.lili.api.modRRHH.modRRHHRublos.RubrosEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rh_rol_detalles")
public class RhRolDetalleEntity {

    @Id
    @Column(name = "id_detalle", unique = true, updatable = false, nullable = false)
    private UUID idDetalle;

    private Long idData;

    private Long idEmpresa;

    private BigDecimal valor;

    @ManyToOne()
    @JoinColumn(name = "id_rubro")
    private RubrosEntity rubros;


}
