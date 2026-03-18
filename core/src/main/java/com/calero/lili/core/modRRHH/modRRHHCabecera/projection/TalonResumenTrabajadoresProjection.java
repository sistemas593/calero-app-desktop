package com.calero.lili.core.modRRHH.modRRHHCabecera.projection;


import java.math.BigDecimal;

public interface TalonResumenTrabajadoresProjection {

    String getCodigo();

    String getRubro();

    BigDecimal getTotalRubro();

    Integer getTotalRegistros();
}
