package com.calero.lili.api.tablas.tbRetenciones;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TbRetencionesGetOneDto {
    private String codigo;
    private String nombreRetencion;
}
