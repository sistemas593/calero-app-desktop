package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoComprobante {


    CI("INGRESO"),
    CE("EGRESO");

    private final String descripcion;
}
