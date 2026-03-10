package com.calero.lili.api.modVentas.projection;


import java.math.BigDecimal;

public interface ImpuestosF104Projection {

    BigDecimal getValor();

    BigDecimal getBaseImponible();

    String getTipoIngreso();

}
