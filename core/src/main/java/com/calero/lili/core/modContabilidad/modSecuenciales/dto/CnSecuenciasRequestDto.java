package com.calero.lili.core.modContabilidad.modSecuenciales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CnSecuenciasRequestDto {

    private String sucursal;
    private Integer anio;
    private Integer mes;
    private Integer ultimoNumero;

}
