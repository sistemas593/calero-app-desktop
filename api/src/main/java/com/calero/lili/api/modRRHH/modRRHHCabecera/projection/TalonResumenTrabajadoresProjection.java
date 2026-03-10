package com.calero.lili.api.modRRHH.modRRHHCabecera.projection;


import java.math.BigDecimal;

public interface TalonResumenTrabajadoresProjection {

    String getCodigo();

    String getRubro();

    BigDecimal getTotalRubro();

    Integer getTotalRegistros();
}
