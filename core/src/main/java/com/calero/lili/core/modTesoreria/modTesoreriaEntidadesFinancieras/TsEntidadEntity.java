package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEstadosCuenta.TsEstadosCuentaEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "ts_entidades")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class TsEntidadEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idEntidad;

    private String tipoEntidad;

    @Column(name = "entidad")
    private String entidad;

    @Column(name = "numero_cuenta")
    private String numeroCuenta;

    @Column(name = "agencia")
    private String agencia;

    @Column(name = "contacto")
    private String contacto;

    @Column(name = "telefono1")
    private String telefono1;

    @Column(name = "telefono2")
    private String telefono2;

    @Column(name = "secuencial_cheque")
    private String secuencialCheque;

    @Column(name = "archivo_cheque")
    private String archivoCheque;

    @Column(name = "saldo")
    private BigDecimal saldo;


    @Builder.Default
    @OneToMany(mappedBy = "tsEntidadEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TsEstadosCuentaEntity> estadoCuentas = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCuenta")
    private CnPlanCuentaEntity planCuenta;

}
