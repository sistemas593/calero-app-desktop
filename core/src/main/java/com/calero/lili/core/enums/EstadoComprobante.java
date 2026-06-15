package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EstadoComprobante {

    B("BORRADOR"),
    A("APROVADO"),
    U("ANULADO");

    private final String nombre;
}
