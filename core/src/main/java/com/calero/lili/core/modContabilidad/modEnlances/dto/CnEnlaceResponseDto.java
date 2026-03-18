package com.calero.lili.core.modContabilidad.modEnlances.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CnEnlaceResponseDto {

    private UUID idEnlace;
    private UUID idCuenta;
    private String detalle;
    private String codigo;

}


