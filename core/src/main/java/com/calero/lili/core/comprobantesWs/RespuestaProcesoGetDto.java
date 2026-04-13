package com.calero.lili.core.comprobantesWs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespuestaProcesoGetDto {

    private UUID idDocumento;
    private String estadoDocumento;
    private Integer emailEstado;
    //private List<Mensajes> mensajes;
    private String numeroAutorizacion;

}
