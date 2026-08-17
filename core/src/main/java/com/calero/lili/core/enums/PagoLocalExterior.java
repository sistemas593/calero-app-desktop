package com.calero.lili.core.enums;

import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PagoLocalExterior {

    L("01"),
    E("02");

    private final String codigo;

    public static PagoLocalExterior getFormaPagoLocExt(String codigoPago) {

        for (PagoLocalExterior formaPago : PagoLocalExterior.values()) {
            if (formaPago.getCodigo().equals(codigoPago)) {
                return formaPago;
            }
        }
        throw new GeneralException("El codigo de la forma exterior o local no existe " + codigoPago);
    }
}
