package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto;

import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.LiquidacionCompra;
import lombok.Data;

import java.util.UUID;


@Data
public class CpComprasXMLLiquidacionesGetDto {

    private UUID idLiquidacion;
    private String estadoDocumento;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private LiquidacionCompra comprobante;

}
