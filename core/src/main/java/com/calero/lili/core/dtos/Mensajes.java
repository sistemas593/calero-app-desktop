package com.calero.lili.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mensajes {

    private String identificador;
    private String mensaje;
    private String informacionAdicional;
    private String tipo;

}
