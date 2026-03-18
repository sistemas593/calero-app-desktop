package com.calero.lili.core.modContabilidad.modReportes.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface BalanceValoresProjection {


    LocalDate getMesFecha();

    UUID getIdCuenta();

    String getCodigoCuenta();

    String getCodigoCuentaOriginal();

    String getCuenta();

    BigDecimal getSaldoInicial();

    BigDecimal getDebe();

    BigDecimal getHaber();

    BigDecimal getSaldoFinal();

    Boolean getMayor();

    Integer getGrupo();

    String getCodigoCentroCostos();

    String getCentroCostos();
}
