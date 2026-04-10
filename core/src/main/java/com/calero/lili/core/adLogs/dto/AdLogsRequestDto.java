package com.calero.lili.core.adLogs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdLogsRequestDto {

    private UUID idDocumento;
    private String serie;
    private String secuencial;
    private String tipoDocumento;
    private Long idData;
    private Long idEmpresa;
    private Boolean validacionPrevia;

}
