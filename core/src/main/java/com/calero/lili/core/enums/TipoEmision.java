package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoEmision {

    NORMAL(1);

    private final Integer codigoEmision;

    public static int obtenerTipoEmision(Integer codigoEmision) {
        for (TipoEmision tipoEmision : TipoEmision.values()) {
            if (tipoEmision.getCodigoEmision().equals(codigoEmision)) {
                return tipoEmision.getCodigoEmision();
            }
        }
        throw new IllegalArgumentException("Código no válido: " + codigoEmision);
    }

}
