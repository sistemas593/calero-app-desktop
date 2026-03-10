package com.calero.lili.api.dtos.deRecibidos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CpImpuestosRecibirFilesCreationRequestDto {

    private List<ListaFiles> listaFiles;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListaFiles {
        // clave acceso utilizo tambien para descargar claves de acceso el mismo dto
//        private String claveAcceso;

//        private String destino;
//        private String periodo;
//        private String fechaRegistro;

        // para recibir archivo XML
        private String nombre;
        private String comprobante;

    }


}
