package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoDocumentoPago {

    PAG("PAGO"),
    RRT("RETENCION RENTA"),
    RIV("RETENCION IVA"),
    NCR("NOTA DE CREDITO");

    private final String nombre;

}
