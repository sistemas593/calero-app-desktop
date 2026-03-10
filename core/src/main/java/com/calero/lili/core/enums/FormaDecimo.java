package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FormaDecimo {

    M("Mensual"),
    A("Anual");

    private final String descripcion;

}
