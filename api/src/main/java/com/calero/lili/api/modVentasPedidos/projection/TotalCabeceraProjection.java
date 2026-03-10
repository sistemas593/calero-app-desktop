package com.calero.lili.api.modVentasPedidos.projection;

import java.math.BigDecimal;


public interface TotalCabeceraProjection {

    BigDecimal getTotalDescuento();

    BigDecimal getTotal();

}
