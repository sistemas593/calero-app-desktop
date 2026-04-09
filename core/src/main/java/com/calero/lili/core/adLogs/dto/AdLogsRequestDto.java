package com.calero.lili.core.adLogs.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AdLogsRequestDto {

    private UUID idDocumento;
    private String serie;
    private String secuencial;
    private String tipoDocumento;
    private Long idData;
    private Long idEmpresa;
    private Boolean validacionPrevia;

}
