package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoContribuyente {
    RE("CONTRIBUYENTE RÉGIMEN RIMPE"),
    RP("CONTRIBUYENTE NEGOCIO POPULAR - RÉGIMEN RIMPE");

    private final String tipoContribuyente;
}
