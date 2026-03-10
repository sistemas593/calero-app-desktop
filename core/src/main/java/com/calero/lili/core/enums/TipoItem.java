package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoItem {
    PRO("Producto"),
    GAS("Gasto"),
    SER("Servicio"),
    ACF("Activo Fijo");

    private final String tipoItem;
}
