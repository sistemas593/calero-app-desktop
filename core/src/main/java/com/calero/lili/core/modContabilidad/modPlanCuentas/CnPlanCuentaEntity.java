package com.calero.lili.core.modContabilidad.modPlanCuentas;

import com.calero.lili.core.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cn_plan_cuentas")
@Where(clause = "deleted = false")
public class CnPlanCuentaEntity extends Auditable implements Persistable<UUID> {

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idCuenta;

    @Override
    public UUID getId() {
        return idCuenta;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    @Column(name = "id_cuenta_padre")
    private UUID idCuentaPadre;

    @Column(name = "codigo_cuenta")
    private String codigoCuenta;

    @Column(name = "codigo_cuenta_original")
    private String codigoCuentaOriginal;

    @Column(name = "cuenta")
    private String cuenta;
         
    @Column(name = "mayor")
    private Boolean mayor;
         
    @Column(name = "nivel")
    private Integer nivel;

    private String tipoAuxiliar;

    private Integer grupo;
}
