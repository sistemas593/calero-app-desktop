package com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.dto;

import com.calero.lili.core.enums.TipoComprobante;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TsComprobanteSecuenciaResponseDto {

    private UUID idComprobanteSecuencia;
    private TipoComprobante tipo;
    private Integer anio;
    private Integer ultimoNumero;
    private UUID idCajas;
    private String nombreCaja;
    private String codigoCaja;

}
