package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CodigoRetencion {


    RTA("1"),
    IVA("2"),
    ISD("3");

    private final String codigo;

}
