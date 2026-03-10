package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Liquidar {
    S("SI LIQUIDAR Mes actual"),
    N("NO LIQUIDAR, LIQUIDA Siguiente mes");
    private final String liquidar;
}
