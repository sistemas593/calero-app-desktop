package com.calero.lili.core.modCompras.modComprasRetenciones.dto;

import com.calero.lili.core.enums.CodigoRetencion;
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
public class CodigoImpuestoResponseDto {

    private UUID idImpuestoCodigo;
    private BigDecimal baseImponible;
    private BigDecimal porcentajeRetener;
    private BigDecimal valorRetenido;
    private String codigoRetencion;
    private CodigoRetencion codigo;


}
