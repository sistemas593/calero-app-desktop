package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CodigoImpuesto {

    MAN("MANUAL SIN CODIGOS DE RETENCION"),
    MAC("MANUAL CON CODIGOS DE RETENCION"),
    IMP("IMPUESTOS SIN CODIGOS DE RETENCION"),
    IMC("IMPUESTOS SIN CODIGOS DE RETENCION"),
    COM("COMPRAS SIN CODIGOS DE RETENCION"),
    COC("COMPRAS CON CODIGOS DE RETENCION"),
    LIQ("LIQUIDACION SIN CODIGOS DE RETENCION"),
    LIC("LIQUIDACION CON CODIGOS DE RETENCION");

    private final String descripcion;
}
