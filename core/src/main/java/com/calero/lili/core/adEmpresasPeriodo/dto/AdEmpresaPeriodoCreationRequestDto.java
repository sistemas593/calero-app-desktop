package com.calero.lili.core.adEmpresasPeriodo.dto;

import lombok.Data;

@Data
public class AdEmpresaPeriodoCreationRequestDto {
    private String ano;
    private String cPeriodo;
    private String periodo;
    private String fechaDesde;
    private String fechaHasta;
}
