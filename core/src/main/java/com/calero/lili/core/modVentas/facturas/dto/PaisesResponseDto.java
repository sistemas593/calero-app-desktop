package com.calero.lili.core.modVentas.facturas.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaisesResponseDto {

    private String nombrePaisOrigen;
    private String nombrePaisDestino;
    private String nombrePaisAdquisicion;

}
