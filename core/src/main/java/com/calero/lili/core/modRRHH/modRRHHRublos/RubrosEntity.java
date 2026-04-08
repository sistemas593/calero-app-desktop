package com.calero.lili.core.modRRHH.modRRHHRublos;


import com.calero.lili.core.enums.TipoRubro;
import com.calero.lili.core.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rh_rubros")
@Where(clause = "deleted = false")
public class RubrosEntity extends Auditable {

    @Id
    @Column(name = "id_rubro", unique = true, updatable = false, nullable = false)
    private UUID idRubro;

    private Long idEmpresa;

    private Long idData;

    private String codigo;

    private String rubro;

    @Enumerated(EnumType.STRING)
    private TipoRubro tipo;

    private Boolean afectaIees;

    private Boolean afectaImpuestoRenta;

    private Boolean esObligatorio;


}
