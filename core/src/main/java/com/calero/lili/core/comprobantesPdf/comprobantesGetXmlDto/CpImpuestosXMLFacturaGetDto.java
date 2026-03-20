package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto;

import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import lombok.Data;

import java.util.UUID;


@Data
public class CpImpuestosXMLFacturaGetDto {

    private UUID idImpuestos;

    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private String tipoDocumento;
    private String destino;
    private Factura comprobante;

}
