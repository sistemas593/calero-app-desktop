package com.calero.lili.api.modVentasCotizaciones.projection;

import java.math.BigDecimal;


public interface TotalesProjection {

    String getCodigo();
    String getCodigoPorcentaje();

    BigDecimal getTotalBaseImponible();

    BigDecimal getTotalValor();

}
