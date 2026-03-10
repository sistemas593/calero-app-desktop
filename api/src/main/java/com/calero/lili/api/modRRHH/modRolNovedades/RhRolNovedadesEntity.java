package com.calero.lili.api.modRRHH.modRolNovedades;


import com.calero.lili.api.modRRHH.RhPeriodosEntity;
import com.calero.lili.api.modRRHH.modRRHHRublos.RubrosEntity;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
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
@Table(name = "rh_rol_novedades")
public class RhRolNovedadesEntity {

    @Id
    @Column(name = "id_novedad", unique = true, updatable = false, nullable = false)
    private UUID idNovedad;

    private Long idData;

    private Long idEmpresa;

    private BigDecimal valor;

    @ManyToOne()
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;

    @ManyToOne()
    @JoinColumn(name = "id_periodo")
    private RhPeriodosEntity periodos;

    @ManyToOne()
    @JoinColumn(name = "id_rubro")
    private RubrosEntity rubros;


}
