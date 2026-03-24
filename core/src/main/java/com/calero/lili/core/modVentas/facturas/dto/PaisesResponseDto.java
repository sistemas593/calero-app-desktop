package com.calero.lili.core.modVentas.facturas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaisesResponseDto {

    private String nombrePaisOrigen;
    private String nombrePaisDestino;
    private String nombrePaisAdquisicion;

}
