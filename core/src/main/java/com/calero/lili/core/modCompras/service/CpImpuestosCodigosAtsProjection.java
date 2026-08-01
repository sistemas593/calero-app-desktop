package com.calero.lili.core.modCompras.service;


import com.calero.lili.core.enums.CodigoRetencion;

import java.math.BigDecimal;
import java.util.UUID;

public interface CpImpuestosCodigosAtsProjection {

    UUID getIdImpuestos();

    BigDecimal getBaseImponible();

    BigDecimal getPorcentajeRetener();

    CodigoRetencion getCodigo();

    String getCodigoRetencion();

    BigDecimal getValorRetenido();

}
