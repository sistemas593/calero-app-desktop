package com.calero.lili.core.modContabilidad.modReportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleMayorDtoPDF {

    private String fechaAsiento;
    private String tipoAsiento;
    private String numeroAsiento;
    private String concepto;
    private String debe;
    private String haber;
    private String saldo;
    private String cuenta;


}
