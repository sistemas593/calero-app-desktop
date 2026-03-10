package com.calero.lili.api.modImpuestosProcesos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImpuestoProcesoResponseDto {

    private String mensaje;
    private String exitoso;

}
