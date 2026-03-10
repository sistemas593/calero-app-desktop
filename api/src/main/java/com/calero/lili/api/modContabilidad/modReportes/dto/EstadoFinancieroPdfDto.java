package com.calero.lili.api.modContabilidad.modReportes.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadoFinancieroPdfDto {


    private String codigoCuenta;
    private String cuenta;
    private String saldoInicial;
    private String enero;
    private String febrero;
    private String marzo;
    private String abril;
    private String mayo;
    private String junio;
    private String julio;
    private String agosto;
    private String septiembre;
    private String octubre;
    private String noviembre;
    private String diciembre;
    private String saldoFinal;
    private Boolean totalMayor;
}
