package com.calero.lili.api.modContabilidad.modPlanCuentas;

import com.calero.lili.api.modAuditoria.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cn_plan_cuentas")
@Where(clause = "deleted = false")
public class CnPlanCuentaEntity extends Auditable {
         
    @Column(name = "id_data")
    private Long idData;
         
    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idCuenta;

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
