package com.calero.lili.core.modAdminPorcentajes.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdIvaPorcentajesDto {

    private Integer tarifaGeneral;
    private Integer tarifaReducida;
    private Integer tarifaConstruccion;
    private String fechaDesde;

}
