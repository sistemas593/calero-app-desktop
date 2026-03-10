package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoDocumentoFactura {

    FAC("FACTURA"),
    SAI("SALDO INICIAL"),
    CAR("CARGO");

    private final String nombre;
}
