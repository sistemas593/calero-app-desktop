package com.calero.lili.api.dtos.deRecibidos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CpImpuestosRecibirResponseDto {

    private String claveAcceso;
    private String archivo;
    private String error;
    private String exitoso;
}
