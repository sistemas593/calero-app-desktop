package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrigenEnum {

    VTS("Ventas"),
    IMP("Impuestos");


    private final String nombre;
}
