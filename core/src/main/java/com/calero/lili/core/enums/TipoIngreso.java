package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoIngreso {
    VL("Ventas locales"),
    AF("Activos fijos"),
    RG("Ingreso por concepto de reembolso de gastos"),
    // EN ESTE CASO SE DEBE VALIDAR QUE EXISTAN LOS SUSTENTOS
    EX("EXPORTACIONES DE BIENES 12 EXPORTACIONES DE SERVICIOS");
    // EN ESTE CASO SE DEBE VALIDAR QUE EXISTAN ESTAS CASILLAS
    private final String tipoIngreso;
}
