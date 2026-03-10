package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoTerceroPerSoc {

    PN("01"),
    SO("02");

    private final String tipoCliente;


}
