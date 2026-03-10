package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoVenta {
    FAC("FACTURA"),
    NDB("NOTA DE DEBITO"),
    NCR("NOTA DE CREDITO"),
    GRM("GUIA REMISION");
    private final String formatoDocumento;
}
