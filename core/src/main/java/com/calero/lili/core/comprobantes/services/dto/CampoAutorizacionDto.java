package com.calero.lili.core.comprobantes.services.dto;

import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampoAutorizacionDto {

    private String formatoDocumento;
    private String versionDocumento;

    private String comprobante;
    private String numeroAutorizacion;
    private String fechaAutorizacion;

    private Factura factura;
    private NotaCredito notaCredito;
    private NotaDebito notaDebito;

    private com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion comprobanteRetencionV1;
    private ComprobanteRetencion comprobanteRetencionV2;

}
