package com.calero.lili.core.tablas.tbRetenciones;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbRetencionesGetOneDto {
    private String codigo;
    private String nombreRetencion;
}
