package com.calero.lili.core.tablas.tbRetencionesCodigos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TbRetencionesCodigosCreationRequestDto {

    private String codigoRetencion;
    private String nombreRetencion;

}
