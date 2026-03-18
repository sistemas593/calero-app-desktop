package com.calero.lili.core.modContabilidad.modCentroCostos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CentroCostosDtoRequest {

    private UUID idCodigoCentroCostosPadre;

    @NotNull(message = "Es requerido el codigo centro de costos")
    private String codigoCentroCostos;

    @NotNull(message = "Es requerido el codigo centro de costos original")
    private String codigoCentroCostosOriginal;

    private String centroCostos;

    @NotNull(message = "Es requerido el mayor")
    private Boolean mayor;


}
