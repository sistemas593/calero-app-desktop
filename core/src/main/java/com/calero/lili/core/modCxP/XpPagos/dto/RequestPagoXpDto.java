package com.calero.lili.core.modCxP.XpPagos.dto;

import com.calero.lili.core.modTesoreria.TsComprabanteEgreso.dto.RequestComprobantesEgresoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestPagoXpDto {

    private UUID idFactura;
    private List<XpPagosRequestDto> pagos;
    private RequestComprobantesEgresoDto comprobantesEgreso;
}
