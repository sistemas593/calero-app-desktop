package com.calero.lili.core.modCxC.XcPagos.dto;

import com.calero.lili.core.modTesoreria.TsComprobanteIngreso.dto.RequestCreationComprobanteIngresoDto;
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
public class RequestPagoDto {

    private UUID idFactura;
    private List<XcPagosDto> pagos;
    private RequestCreationComprobanteIngresoDto comprobanteIngreso;

}
