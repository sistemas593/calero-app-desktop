package com.calero.lili.core.modCxC.XcRetenciones.dto;

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
public class RequestRetencionesDto {

    private UUID idFactura;
    private List<XcRetencionesDto> pagos; // recorrer la lista de retencion
    private List<UUID> retencionesValores;

    // ENTIDAD RETENCIONES VALORES RELACIONAR CON EL UUDI DE LA XcFacturasEntity


}
