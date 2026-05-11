package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoClienteProveedor {

    N("01"),
    J("02");

    private final String tipo;

}
