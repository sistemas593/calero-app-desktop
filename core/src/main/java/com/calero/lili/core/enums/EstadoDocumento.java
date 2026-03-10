package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EstadoDocumento {
    PEN("PENDIENTE"),
    ENV("ENVIAR"),
    REC("RECIBIDA"),
    AUT("AUTORIZADA"),
    DEV("DEVUELTA"),
    NOA("NO AUTORIZDA");
    private final String estadoDocumento;
}
