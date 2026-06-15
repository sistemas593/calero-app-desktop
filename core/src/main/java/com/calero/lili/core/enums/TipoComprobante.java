package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoComprobante {


    I("INGRESO"),
    E("EGRESO");

    private final String descripcion;
}
