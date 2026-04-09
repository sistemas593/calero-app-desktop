package com.calero.lili.core.comprobantesWs;

import com.calero.lili.core.dtos.Mensajes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespuestaProceso {

    private String estadoEnvio;
    private String estadoAutorizacion;
    private Integer emailEstado;
    private List<Mensajes> mensajes;
    private List<Mensajes> mensajesRecepcion;
    private String comprobante;
    private String numeroAutorizacion;
    private String fechaAutorizacion;

}
