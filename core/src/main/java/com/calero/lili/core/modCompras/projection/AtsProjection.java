package com.calero.lili.core.modCompras.projection;


import com.calero.lili.core.enums.DocumentoEnum;

import java.math.BigDecimal;

public interface AtsProjection {
    DocumentoEnum getDocumento();
    BigDecimal getBiTarifaCero();
    BigDecimal getBiBaseDiferenteCero();
    BigDecimal getBiBaseNoObjectoIva();
    BigDecimal getValorIva();
    Integer getTotalRegistros();
}
