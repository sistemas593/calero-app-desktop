package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DestinoEnum {

    IV("IVA"),
    GP("GASTOS PERSONALES"),
    NI("NO INGRESAR");

    private final String nombre;
}
