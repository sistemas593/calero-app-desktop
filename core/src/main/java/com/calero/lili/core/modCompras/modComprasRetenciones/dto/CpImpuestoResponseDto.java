package com.calero.lili.core.modCompras.modComprasRetenciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpImpuestoResponseDto {

    private String serie;
    private String secuencial;
    private String fechaEmision;
    private String numeroAutorizacion;

}
