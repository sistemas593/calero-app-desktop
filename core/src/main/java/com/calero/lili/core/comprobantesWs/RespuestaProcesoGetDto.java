package com.calero.lili.core.comprobantesWs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespuestaProcesoGetDto {

    private String estadoDocumento;
    private Integer emailEstado;
    //private List<Mensajes> mensajes;
    private String numeroAutorizacion;

}
