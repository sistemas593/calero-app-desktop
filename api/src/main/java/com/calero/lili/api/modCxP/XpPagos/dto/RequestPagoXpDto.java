package com.calero.lili.api.modCxP.XpPagos.dto;

import com.calero.lili.api.modTesoreria.TsComprabanteEgreso.dto.RequestComprobantesEgresoDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RequestPagoXpDto {

    private UUID idFactura;
    private List<XpPagosRequestDto> pagos;
    private RequestComprobantesEgresoDto comprobantesEgreso;
}
