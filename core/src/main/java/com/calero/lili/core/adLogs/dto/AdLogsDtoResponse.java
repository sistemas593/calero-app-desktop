package com.calero.lili.core.adLogs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdLogsDtoResponse {

    private UUID id;
    private String mensajes;
    private String serie;
    private String secuencial;
    private String tipoDocumento;
    private String fechaHora;
    private String tipo;

}
