package com.calero.lili.core.modVentasRetenciones.projection;

import java.math.BigDecimal;


public interface TotalesProjection {

    String getCodigo();

    String getCodigoRetencion();

    BigDecimal getTotalBaseImponible();

    BigDecimal getTotalValor();

}
