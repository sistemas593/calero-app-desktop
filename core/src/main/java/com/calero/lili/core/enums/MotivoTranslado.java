package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MotivoTranslado {


    VEN("Venta"),
    COM("Compra"),
    TRA("Transformación"),
    CON("Consignación"),
    TEE("Traslado Entre Establecimientos"),
    TEI("Traslado Emisión Itinerante"),
    DEV("Devolución"),
    IMP("Importación"),
    EXP("Exportación"),
    OTR("Otros");

    private final String nombre;
}
