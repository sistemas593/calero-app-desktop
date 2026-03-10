package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoTercero {

    CLIENTE(1),
    PROVEEDOR(2),
    TRANSPORTISTA(3),
    TRABAJADOR(4);

    private final Integer tipo;

    public static String obtenerTipo(Integer tipo) {
        for (TipoTercero tipoTercero : TipoTercero.values()) {
            if (tipoTercero.getTipo().equals(tipo)) {
                return tipoTercero.name();
            }
        }
        throw new IllegalArgumentException("Código no válido: " + tipo);
    }

}
