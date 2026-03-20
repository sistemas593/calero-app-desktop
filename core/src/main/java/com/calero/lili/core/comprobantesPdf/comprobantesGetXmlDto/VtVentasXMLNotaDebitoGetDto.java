package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto;

import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import lombok.Data;

import java.util.UUID;


@Data
public class VtVentasXMLNotaDebitoGetDto {

    private UUID idVenta;
    private String estadoDocumento;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private NotaDebito comprobante;

}
