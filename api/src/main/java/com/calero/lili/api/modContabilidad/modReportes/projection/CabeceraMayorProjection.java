package com.calero.lili.api.modContabilidad.modReportes.projection;

import java.math.BigDecimal;

public interface CabeceraMayorProjection {

    String getCodigoCuenta();

    String getCuenta();

    BigDecimal getSaldoInicial();
}
