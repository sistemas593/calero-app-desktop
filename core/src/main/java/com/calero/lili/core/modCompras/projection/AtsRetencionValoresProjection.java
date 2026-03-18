package com.calero.lili.core.modCompras.projection;

import java.math.BigDecimal;

public interface AtsRetencionValoresProjection {

    BigDecimal getRetencionValor10();
    BigDecimal getRetencionValor20();
    BigDecimal getRetencionValor30();
    BigDecimal getRetencionValor50();
    BigDecimal getRetencionValor70();
    BigDecimal getRetencionValor100();
}
