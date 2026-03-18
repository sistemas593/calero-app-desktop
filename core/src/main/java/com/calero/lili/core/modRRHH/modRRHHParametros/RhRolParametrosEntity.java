package com.calero.lili.core.modRRHH.modRRHHParametros;


import com.calero.lili.core.modRRHH.modRRHHRublos.RubrosEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
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
@Table(name = "rh_rol_parametro")
public class RhRolParametrosEntity {

    @Id
    @Column(name = "id_parametro", unique = true, updatable = false, nullable = false)
    private UUID idParametro;

    private Long idData;

    private Long idEmpresa;

    private String anio;

    private BigDecimal valor;

    @ManyToOne()
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;

    @ManyToOne()
    @JoinColumn(name = "id_rubro")
    private RubrosEntity rubros;


}
