package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Sustentos {
    S01("01","CREDITO TRIBUTARIO"),
    S02("02","COSTO O GASTO"),
    S03("03","XX"),
    S04("04","SSS"),
    S05("05","SSS"),
    S06("07","SSSS"),
    ;

    private final String codigoSustento;
    private final String nombreSustento;

    public static Sustentos fromCodigo(String codigo) throws Exception {
        for (Sustentos nombre : Sustentos.values()) {
            if (nombre.getCodigoSustento().equals(codigo)) {
                return nombre;
            }
        }
        throw new Exception("Código documento no válido: " + codigo);
    }

}
