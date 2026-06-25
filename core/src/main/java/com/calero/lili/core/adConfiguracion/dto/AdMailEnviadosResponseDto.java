package com.calero.lili.core.adConfiguracion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdMailEnviadosResponseDto {

    private Long id;
    private String clave1;
    private String codigoDocumento;
    private String serie;
    private String secuencial;
    private String mailTo;
    private String fecha;
    private Long total;
}
