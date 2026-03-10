package com.calero.lili.api.dtos.deRecibidos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CpImpuestosRecibirListExistRequestResponseDto {

    private List<ListaClavesAcceso> listaClavesAcceso;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListaClavesAcceso {
        // clave acceso utilizo tambien para descargar claves de acceso el mismo dto
        private String claveAcceso;
        // para verificar si ya existe
        private String existe;

    }

}
