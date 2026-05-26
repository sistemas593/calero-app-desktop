package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrigenImpuestos {

    DSC("DESTINO SIN CODIGOS DE RETENCION"),
    ISC("IMPUESTOS SIN CODIGOS DE RETENCION"),
    ICC("IMPUESTOS CON CODIGOS DE RETENCION"),
    CSC("COMPRAS SIN CODIGOS DE RETENCION"),
    CCC("COMPRAS CON CODIGOS DE RETENCION"),
    LSC("LIQUIDACION SIN CODIGOS DE RETENCION"),
    LCC("LIQUIDACION CON CODIGOS DE RETENCION"),
    NSC("NOTAS DE CREDITO SIN CODIGOS DE RETENCION"),
    RCC("RETENCION CON CODIGOS DE RETENCION"),
    XDF("POR DEFINIR");

    private final String descripcion;


    // cuando se clave de acceso y xml recibidos los que van a compras impuestos
}
