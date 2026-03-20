package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto;

import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import lombok.Data;

import java.util.UUID;


@Data
public class CpComprasXMLRetencionGetDto {

    private UUID idRetencion;
    private String estadoDocumento;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private ComprobanteRetencion comprobante;

}
