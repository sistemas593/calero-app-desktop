package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto;

import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import lombok.Data;

import java.util.UUID;

@Data
public class VtVentasXMLFacturaGetDto {

    private UUID idVenta;
    private String estadoDocumento;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private Factura comprobante;

}
