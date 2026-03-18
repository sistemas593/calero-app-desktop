package com.calero.lili.core.adEmpresasPeriodo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class AdEmpresaPeriodoCreationResponseDto {

    private UUID idPeriodo;
    private String ano;
    private String cPeriodo;
    private String periodo;
    private String fechaDesde;
    private String fechaHasta;
}
