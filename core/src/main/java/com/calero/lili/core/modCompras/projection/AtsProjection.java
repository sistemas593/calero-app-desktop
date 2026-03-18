package com.calero.lili.core.modCompras.projection;


import java.math.BigDecimal;

public interface AtsProjection {
    String getCodigoDocumento();
    BigDecimal getBiTarifaCero();
    BigDecimal getBiBaseDiferenteCero();
    BigDecimal getBiBaseNoObjectoIva();
    BigDecimal getValorIva();
    Integer getTotalRegistros();
}
