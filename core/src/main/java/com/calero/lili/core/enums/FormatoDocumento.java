package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FormatoDocumento {
    F("FISICO"),
    E("ELECTRONICO");
    private final String formatoDocumento;
}
