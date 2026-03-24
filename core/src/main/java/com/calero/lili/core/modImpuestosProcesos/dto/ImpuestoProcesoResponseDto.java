package com.calero.lili.core.modImpuestosProcesos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImpuestoProcesoResponseDto {

    private String mensaje;
    private String exitoso;

}
