package com.calero.lili.core.comprobantesWs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArchivoDto {

    private String nombre;
    private byte[] contenido;
}
