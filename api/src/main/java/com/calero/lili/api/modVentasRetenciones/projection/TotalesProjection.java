package com.calero.lili.api.modVentasRetenciones.projection;

import java.math.BigDecimal;


public interface TotalesProjection {

    String getCodigo();

    String getCodigoRetencion();

    BigDecimal getTotalBaseImponible();

    BigDecimal getTotalValor();

}
