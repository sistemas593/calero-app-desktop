package com.calero.lili.core.modCompras.projection;

import java.math.BigDecimal;

public interface AtsRetencionResumenProjection {

    String getCodigoRetencion();
    String getConceptoRetencion();
    BigDecimal getBaseImponible();
    BigDecimal getValorRetenido();
    Integer getRegistros();
}
