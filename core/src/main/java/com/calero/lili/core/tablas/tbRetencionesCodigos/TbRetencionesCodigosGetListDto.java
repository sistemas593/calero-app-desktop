package com.calero.lili.core.tablas.tbRetencionesCodigos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TbRetencionesCodigosGetListDto {

    private String codigoRetencion;
    private String nombreRetencion;

}
