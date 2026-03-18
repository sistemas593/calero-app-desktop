package com.calero.lili.core.adEmpresasPeriodo.dto;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public class AdEmpresaPeriodoListFilterDto {


    private UUID idPeriodo;
    private String ano;
    private String cPeriodo;
    private String periodo;
    private String fechaDesde;
    private String fechaHasta;

}
