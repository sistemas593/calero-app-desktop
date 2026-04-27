package com.calero.lili.core.dtos.deRecibidos;

import com.calero.lili.core.enums.DestinoEnum;
import lombok.Data;

@Data
public class CpImpuestosRecibirCreationRequestDto {

    // clave acceso utilizo tambien para descargar claves de acceso el mismo dto
    private String claveAcceso;

    private DestinoEnum destino;
//    private String periodo;
    private String codigoSustento;
    private String fechaRegistro;

    // para recibir archivo XML
    private String nombre;
    private String comprobante;

    // para verificar si ya existe
    private String existe;

}
