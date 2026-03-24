package com.calero.lili.core.tablas.tbRetencionesCodigos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbRetencionesCodigosCreationRequestDto {

    private String codigoRetencion;
    private String nombreRetencion;

}
