package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FormaPago {

    CR("CREDITO"),
    CO("CONTADO"),;

    private final String formaPago;

}
