package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SexoEnum {

    M("Masculino"),
    F("Femenino");

    private final String sexo;

}
