package com.calero.lili.core.modRRHH.modRRHHCabecera;


import com.calero.lili.core.Auditable;
import com.calero.lili.core.modRRHH.RhPeriodosEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.util.List;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rh_rol_cabecera")
@Where(clause = "deleted = false")
public class RhRolCabeceraEntity extends Auditable {

    @Id
    @Column(name = "id_rol", unique = true, updatable = false, nullable = false)
    private UUID idRol;

    private Long idData;

    private Long idEmpresa;

    private Integer diasTrabajados;

    private BigDecimal sueldoBase;

    private BigDecimal totalIngresos;

    private BigDecimal totalDeducciones;

    private BigDecimal netoPagar;

    private LocalDate fechaGeneracion;

    @ManyToOne()
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;

    @ManyToOne()
    @JoinColumn(name = "id_periodo")
    private RhPeriodosEntity periodos;

    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RhRolDetalleEntity> detalles;

}
