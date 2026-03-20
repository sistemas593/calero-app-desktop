package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto;

import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import lombok.Data;

import java.util.UUID;


@Data
public class CpImpuestosXMLNotaDebitoGetDto {

    private UUID idImpuestos;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private String tipoDocumento;
    private String destino;
    private NotaDebito comprobante;

}
