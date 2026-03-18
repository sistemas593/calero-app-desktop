package com.calero.lili.core.modComprasProveedoresGrupos;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;


@Data
@Entity
@Table(name = "cp_proveedores_grupos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class CpProveedoresGruposEntity  extends Auditable {
         
    @Column(name = "id_data")
    private Long idData;
         
    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idGrupo;

    @Column(name = "grupo")
    private String grupo;

    private Boolean predeterminado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCuentaCredito", referencedColumnName = "idCuenta")
    private CnPlanCuentaEntity cuentaCredito;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCuentaAnticipo", referencedColumnName = "idCuenta")
    private CnPlanCuentaEntity cuentaAnticipo;
}
