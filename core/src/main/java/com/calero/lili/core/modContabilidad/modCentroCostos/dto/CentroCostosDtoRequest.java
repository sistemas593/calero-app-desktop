package com.calero.lili.core.modContabilidad.modCentroCostos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;


@Data
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
