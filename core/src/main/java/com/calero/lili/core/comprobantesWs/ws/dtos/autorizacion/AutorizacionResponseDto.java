package com.calero.lili.core.comprobantesWs.ws.dtos.autorizacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class AutorizacionResponseDto {
    private String claveAccesoConsultada;
    private Integer numeroComprobantes;
    private List<Autorizaciones> autorizaciones;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Autorizaciones {
        private String estado;
        private String numeroAutorizacion;
        private String fechaAutorizacion;
        private String ambiente;
        private String comprobante;
        private String mensaje;
    }

}
