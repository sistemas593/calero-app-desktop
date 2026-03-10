package com.calero.lili.api.modComprasOrden.projection;

import java.math.BigDecimal;


public interface TotalesProjection {

    String getCodigo();
    String getCodigoPorcentaje();

    BigDecimal getTotalBaseImponible();

    BigDecimal getTotalValor();

}
