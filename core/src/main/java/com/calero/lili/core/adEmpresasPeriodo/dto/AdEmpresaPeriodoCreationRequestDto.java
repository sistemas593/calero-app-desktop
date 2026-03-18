package com.calero.lili.core.adEmpresasPeriodo.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AdEmpresaPeriodoCreationRequestDto {
    private String ano;
    private String cPeriodo;
    private String periodo;
    private String fechaDesde;
    private String fechaHasta;
}
