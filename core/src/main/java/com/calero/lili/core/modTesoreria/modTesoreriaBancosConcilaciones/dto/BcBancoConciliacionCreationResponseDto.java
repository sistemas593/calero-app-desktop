package com.calero.lili.core.modTesoreria.modTesoreriaBancosConcilaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BcBancoConciliacionCreationResponseDto {

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
