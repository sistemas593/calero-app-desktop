package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailEstado {

    NO_ENTREGADO(1),
    ENTREGADO(2),
    REBOTADO(3);

    private final Integer tipo;


    public static Integer obtenerTipo(Integer tipo) {
        for (EmailEstado emailEstado : EmailEstado.values()) {
            if (emailEstado.getTipo().equals(tipo)) {
                return emailEstado.getTipo();
            }
        }
        throw new IllegalArgumentException("Código no válido: " + tipo);
    }
}
