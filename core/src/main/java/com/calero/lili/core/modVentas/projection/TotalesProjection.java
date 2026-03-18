package com.calero.lili.core.modVentas.projection;

import java.math.BigDecimal;


public interface TotalesProjection {

    String getCodigo();
    String getCodigoPorcentaje();

    BigDecimal getTotalBaseImponible();

    BigDecimal getTotalValor();

}
