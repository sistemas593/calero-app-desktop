package com.calero.lili.core.apiSitac.dtos.stFechaActualizacion;

import lombok.Data;

@Data
public class AdStFechaActualizacionGetDto {

    private String idSistema;
    private String fechaActualizacion;
    private String fechaVencimiento;
    private String enviarCorreos;
    private String link;
    private String infoRuc;

    private String modulos;
    private String rucsActivados;
    private String clavesPcs;

    private String tipoBlo;
    //private String documentosElectronicosBlo;
    //private String fechaBlo;
}
