package com.calero.lili.api.adEmpresasPeriodo.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class AdEmpresaPeriodoReportDto {

    private Long idPeriodo;

    private String ano;

    private String cperiodo;

    private String periodo;

    private LocalDate fechaDesde;

    private LocalDate fechaHasta;

}
