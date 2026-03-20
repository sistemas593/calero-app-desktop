package com.calero.lili.core.comprobantesWs.ws.dtos.recepcion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class RecepcionResponseDto {

    private String estado;
    private List<Comprobantes> comprobantes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Comprobantes {
        private String claveAcceso;
        private List<Mensajes> mensajes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Mensajes {
        private String identificador;
        private String mensaje;
        private String informacionAdicional;
        private String tipo;

    }
}
