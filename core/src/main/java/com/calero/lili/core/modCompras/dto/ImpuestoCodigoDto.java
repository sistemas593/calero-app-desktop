package com.calero.lili.core.modCompras.dto;

import com.calero.lili.core.enums.CodigoRetencion;
import com.calero.lili.core.tablas.tbRetencionesCodigos.TbRetencionesCodigosGetOneDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImpuestoCodigoDto {

    private UUID idImpuestos;
    private BigDecimal baseImponible;
    private BigDecimal porcentajeRetener;
    private BigDecimal valorRetenido;
    private CodigoRetencion codigo;
    private String codigoRetencion;

}
