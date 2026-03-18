package com.calero.lili.core.modContabilidad.modReportes.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface SaldoInicialBCProjection {

    UUID getIdCuenta();

    String getCodigoCuenta();

    String getCodigoCuentaOriginal();

    String getCuenta();

    BigDecimal getSaldoInicial();

    Boolean getMayor();

}
