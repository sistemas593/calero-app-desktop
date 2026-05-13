package com.calero.lili.core.dtos.errors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Builder
@Setter
public class DetalleError {
    private int linea;
    private EnumError type;
    private String detalle;
}
