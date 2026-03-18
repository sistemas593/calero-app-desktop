package com.calero.lili.core.modCompras.modComprasLiquidaciones.projection;

import java.math.BigDecimal;


public interface TotalesProjection {

    String getCodigo();
    String getCodigoPorcentaje();

    BigDecimal getTotalBaseImponible();

    BigDecimal getTotalValor();

}
