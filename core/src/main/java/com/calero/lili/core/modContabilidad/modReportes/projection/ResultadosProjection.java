package com.calero.lili.core.modContabilidad.modReportes.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ResultadosProjection {

    LocalDate getMesFecha();

    BigDecimal getResultado();

}
