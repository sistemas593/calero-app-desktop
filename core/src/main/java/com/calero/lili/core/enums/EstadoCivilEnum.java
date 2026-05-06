package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EstadoCivilEnum {


    S("Soltero/a"),
    C("Casado/a"),
    D("Divorciado/a"),
    V("Viudo/A"),
    U("Unión libre");

    private final String estadoCivil;

}
