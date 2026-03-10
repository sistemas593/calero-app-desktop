package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Ambiente {
    PRUEBAS(1),
    PRODUCCCION(2);

    private final Integer ambiente;

    public static int obtenerAmbiente(Integer ambiente) {
        for (Ambiente tipo : Ambiente.values()) {
            if (tipo.getAmbiente().equals(ambiente)) {
                return tipo.getAmbiente();
            }
        }
        throw new IllegalArgumentException("Código no válido: " + ambiente);
    }
}
