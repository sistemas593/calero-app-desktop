package com.calero.lili.core.enums;

import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CodigoRetencion {


    RTA("1"),
    IVA("2"),
    ISD("3");

    private final String codigo;

    public static CodigoRetencion fromCodigo(String codigo) {
        for (CodigoRetencion nombre : CodigoRetencion.values()) {
            if (nombre.getCodigo().equals(codigo)) {
                return nombre;
            }
        }
        throw new GeneralException("Código de retención no  es válido: " + codigo);
    }

}
