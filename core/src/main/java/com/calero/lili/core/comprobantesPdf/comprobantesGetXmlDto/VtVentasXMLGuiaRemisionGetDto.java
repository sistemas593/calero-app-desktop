package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto;

import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.GuiaRemision;
import lombok.Data;

import java.util.UUID;


@Data
public class VtVentasXMLGuiaRemisionGetDto {

    private UUID idGuia;
    private String estadoDocumento;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private GuiaRemision comprobante;

}
