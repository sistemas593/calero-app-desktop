package com.calero.lili.api.modCompras.dto;

import com.calero.lili.api.tablas.tbRetenciones.TbRetencionesGetOneDto;
import com.calero.lili.api.tablas.tbRetencionesCodigos.TbRetencionesCodigosGetOneDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ImpuestoCodigoDto {

    private UUID idImpuestos;
    private String codigo;
    private String codigoRetencion;
    private BigDecimal baseImponible;
    private BigDecimal porcentajeRetener;
    private BigDecimal valorRetenido;
    private TbRetencionesGetOneDto retencion;
    private TbRetencionesCodigosGetOneDto retencionCodigo;


}
