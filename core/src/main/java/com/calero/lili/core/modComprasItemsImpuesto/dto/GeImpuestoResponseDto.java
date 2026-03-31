package com.calero.lili.core.modComprasItemsImpuesto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeImpuestoResponseDto {

    private Long idImpuesto;
    private String codigo;
    private String codigoPorcentaje;
    private BigDecimal tarifa;


}
