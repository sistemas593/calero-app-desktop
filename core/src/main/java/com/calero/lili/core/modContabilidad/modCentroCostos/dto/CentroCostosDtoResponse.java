package com.calero.lili.core.modContabilidad.modCentroCostos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CentroCostosDtoResponse {

    private UUID idCentroCostos;
    private UUID idCodigoCentroCostosPadre;
    private String nombreCodigoCentroPadre;
    private String codigoCentroCostos;
    private String codigoCentroCostosOriginal;
    private String centroCostos;
    private Boolean mayor;
    private Integer nivel;


}
