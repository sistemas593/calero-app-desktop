package com.calero.lili.core.modVentasCotizaciones.projection;

import java.math.BigDecimal;

public interface OneDetalleProjection {

    String getIdItem();
    String getCodigoPrincipal();
    String getCodigoAuxiliar();

    String getCodigoBarras();

    String getUnidadMedida();

    String getDescripcion();

    BigDecimal getPrecioUnitario();

    String getCantidad();

    BigDecimal getDsctoItem();
    BigDecimal getDescuento();

    BigDecimal getSubtotalItem();

    String getImpuestos();

    String getDetallesAdicionales();

}
