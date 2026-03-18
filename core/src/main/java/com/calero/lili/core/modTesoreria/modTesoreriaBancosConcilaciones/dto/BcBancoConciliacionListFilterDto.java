package com.calero.lili.core.modTesoreria.modTesoreriaBancosConcilaciones.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@ToString
public class BcBancoConciliacionListFilterDto {

    private UUID idConciliacion;
    private String fechaCorte;
    private BigDecimal conciliadoSaldoInicial;
    private BigDecimal conciliadoDepositos;
    private BigDecimal conciliadoNotasCredito;
    private BigDecimal conciliadoNotasDebito;
    private BigDecimal conciliadoCheques;
    private BigDecimal conciliadoSaldoFinal;
    private BigDecimal estadoCuentaDepositos;
    private BigDecimal estadoCuentaNotasCredito;
    private BigDecimal estadoCuentaNotasDebito;
    private BigDecimal estadoCuentaCheques;

}
