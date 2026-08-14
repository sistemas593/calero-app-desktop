package com.calero.lili.core.modImpuestosAnexos.formulario103.projection;


import java.math.BigDecimal;

public interface Formulario103Projection {

    String getCodigoFormulario();

    BigDecimal getBaseImponible();

    BigDecimal getValorRetenido();

}
