package com.calero.lili.core.adConfiguracion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdMailEnviadosTotalResponseDto {

    private Long id;
    private String periodo;
    private String clave1;
    private Long total;
}
