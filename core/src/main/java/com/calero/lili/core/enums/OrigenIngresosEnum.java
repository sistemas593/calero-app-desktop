package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrigenIngresosEnum {


    B("Empleado público"),
    V(" Empleado privado"),
    I("Independiente"),
    A("Ama de casa o estudiante"),
    R("Rentista"),
    H("Jubilado"),
    M("Remesas del exterior");


    private final String estadoCivil;
}
