package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoRubro {

    I("Ingreso"),
    E("Egreso"),
    P("Provision"),
    G("Gastos Personales"),
    X("Exoneracion"),
    O("Otros Empleadores"),
    R("Retenciones"),
    B("BASE IMPONIBLE"),

    ;

    private final String descripcion;
}
