package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoDocumentoSerie {


    FAC("FACTURA"),
    NDB("NOTA DE DEBITO"),
    NCR("NOTA DE CREDITO"),
    GRM("GUIA REMISION"),
    CRT("COMPROBANTE DE RETENCION"),
    LIQ("LIQUIDACIONES DE COMPRA");

    private final String descripcion;
}
