package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto;

import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import lombok.Data;

import java.util.UUID;


@Data
public class VtVentasXMLNotaCreditoGetDto {

    private UUID idVenta;
    private String estadoDocumento;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private NotaCredito comprobante;

}
