package com.calero.lili.core.modContabilidad.modPlanCuentas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilaReporteMayorContableDto {

    private String fechaAsiento;
    private String numeroAsiento;
    private String concepto;

    private String codigoCuenta;
    private String nombreCuenta;
    private String numeroDocumento;
    private String tipoDocumento;

    private BigDecimal debe;
    private BigDecimal haber;

}
