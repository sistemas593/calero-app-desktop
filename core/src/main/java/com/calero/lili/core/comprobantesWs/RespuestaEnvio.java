package com.calero.lili.core.comprobantesWs;

import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.enums.EstadoDocumento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespuestaEnvio {

    private EstadoDocumento estadoEnvio;
    private List<Mensajes> mensajes;

}
