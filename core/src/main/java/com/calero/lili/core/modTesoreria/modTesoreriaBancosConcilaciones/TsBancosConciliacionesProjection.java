package com.calero.lili.core.modTesoreria.modTesoreriaBancosConcilaciones;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public interface TsBancosConciliacionesProjection {

    String getIdData();
    void setIdData(Long idData);

    String getIdEmpresa();
    void setIdEmpresa(Long idEmpresa);

    UUID getIdConciliacion();
    void setIdConciliacion(UUID idConciliacion);

    LocalDate getFechaCorte();
    void setFechaCorte(int fechaCorte);

    BigDecimal getConciliadoSaldoInicial();
    void setConciliadoSaldoInicial(BigDecimal conciliadoSaldoInicial);

    BigDecimal getConciliadoDepositos();
    void setConciliadoDepositos(BigDecimal conciliadoDepositos);

    BigDecimal getConciliadoNotasCredito();
    void setConciliadoNotasCredito(BigDecimal conciliadoNotasCredito);

    BigDecimal getConciliadoNotasDebito();
    void setConciliadoNotasDebito(BigDecimal conciliadoNotasDebito);

    BigDecimal getConciliadoCheques();
    void setConciliadoCheques(BigDecimal conciliadoCheques);

    BigDecimal getConciliadoSaldoFinal();
    void setConciliadoSaldoFinal(BigDecimal conciliadoSaldoFinal);

    BigDecimal getEstadoCuentaDepositos();
    void setEstadoCuentaDepositos(BigDecimal estadoCuentaDepositos);

    BigDecimal getEstadoCuentaNotasCredito();
    void setEstadoCuentaNotasCredito(BigDecimal estadoCuentaNotasCredito);

    BigDecimal getEstadoCuentaNotasDebito();
    void setEstadoCuentaNotasDebito(BigDecimal estadoCuentaNotasDebito);

    BigDecimal getEstadoCuentaCheques();
    void setEstadoCuentaCheques(BigDecimal estadoCuentaCheques);
}
