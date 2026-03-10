package com.calero.lili.api.modClientesConfiguraciones.dto;

import lombok.Data;

@Data
public class StCorreoRequestDto {

    private String to;
    private String pdf;
    private String xml;

    private String mailFrom;
    private String nombreEmisor;
    private String rucEmisor;
    private String nombreReceptor;
    private String codigoDocumento;
    private String serie;
    private String secuencia;
    private String fechaEmision;
    private String claveAcceso;

}
