package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PagoLocalExterior {

    L("01"),
    E("02");

    private final String codigo;
}
