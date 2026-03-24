package com.calero.lili.core.adEmpresasPeriodo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdEmpresaPeriodoCreationResponseDto {

    private UUID idPeriodo;
    private String ano;
    private String cPeriodo;
    private String periodo;
    private String fechaDesde;
    private String fechaHasta;
}
