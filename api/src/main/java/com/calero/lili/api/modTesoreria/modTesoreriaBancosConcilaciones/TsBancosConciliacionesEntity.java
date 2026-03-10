package com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones;

import com.calero.lili.core.Auditable;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
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
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ts_bancos_conciliaciones")
@Where(clause = "deleted = false")
public class TsBancosConciliacionesEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idConciliacion;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "fecha_corte")
    private LocalDate fechaCorte;

    @Column(name = "conciliado_saldo_inicial")
    private BigDecimal conciliadoSaldoInicial;

    @Column(name = "conciliado_depositos")
    private BigDecimal conciliadoDepositos;

    @Column(name = "conciliado_notas_credito")
    private BigDecimal conciliadoNotasCredito;

    @Column(name = "conciliado_notas_debito")
    private BigDecimal conciliadoNotasDebito;

    @Column(name = "conciliado_cheques")
    private BigDecimal conciliadoCheques;

    @Column(name = "conciliado_saldo_final")
    private BigDecimal conciliadoSaldoFinal;

    @Column(name = "estado_cuenta_depositos")
    private BigDecimal estadoCuentaDepositos;

    @Column(name = "estado_cuenta_notas_credito")
    private BigDecimal estadoCuentaNotasCredito;

    @Column(name = "estado_cuenta_notas_debito")
    private BigDecimal estadoCuentaNotasDebito;

    @Column(name = "estado_cuenta_cheques")
    private BigDecimal estadoCuentaCheques;

    @ManyToOne()
    @JoinColumn(name = "idEntidad", referencedColumnName = "idEntidad")
    private TsEntidadEntity tsEntidadEntity;

}
