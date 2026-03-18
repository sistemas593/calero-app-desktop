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

    private Integer iva1;
    private Integer iva2;
    private Integer iva3;
    private String fechaDesde;

}
