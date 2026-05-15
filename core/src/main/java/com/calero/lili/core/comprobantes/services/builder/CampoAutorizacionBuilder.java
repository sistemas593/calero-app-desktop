package com.calero.lili.core.comprobantes.services.builder;

import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.comprobantes.services.dto.CampoAutorizacionDto;
import com.calero.lili.core.comprobantes.utils.XmlUtils;
import org.springframework.stereotype.Component;

@Component
public class CampoAutorizacionBuilder {


    public CampoAutorizacionDto builder(Autorizacion documento) {
        return CampoAutorizacionDto.builder()
                .numeroAutorizacion(documento.getNumeroAutorizacion())
                .fechaAutorizacion(documento.getFechaAutorizacion())
                .comprobante(documento.getComprobante())
                .build();
    }

    public CampoAutorizacionDto builderFactura(Factura documento) {
        return CampoAutorizacionDto.builder()
                .numeroAutorizacion(documento.getInfoTributaria().getClaveAcceso())
                .fechaAutorizacion(null)
                .factura(documento)
                .comprobante(XmlUtils.convertToXmlString(Factura.class, documento))
                .build();
    }

    public CampoAutorizacionDto builderNotaCredito(NotaCredito documento) {
        return CampoAutorizacionDto.builder()
                .numeroAutorizacion(documento.getInfoTributaria().getClaveAcceso())
                .fechaAutorizacion(null)
                .notaCredito(documento)
                .comprobante(XmlUtils.convertToXmlString(NotaCredito.class, documento))
                .build();
    }

    public CampoAutorizacionDto builderNotaDebito(NotaDebito documento) {
        return CampoAutorizacionDto.builder()
                .numeroAutorizacion(documento.getInfoTributaria().getClaveAcceso())
                .fechaAutorizacion(null)
                .notaDebito(documento)
                .comprobante(XmlUtils.convertToXmlString(NotaDebito.class, documento))
                .build();
    }

    public CampoAutorizacionDto builderComprobanteRetencionV1(ComprobanteRetencion retencionUno) {
        return CampoAutorizacionDto.builder()
                .numeroAutorizacion(retencionUno.getInfoTributaria().getClaveAcceso())
                .fechaAutorizacion(null)
                .comprobanteRetencionV1(retencionUno)
                .comprobante(XmlUtils.convertToXmlString(ComprobanteRetencion.class, retencionUno))
                .build();
    }

    public CampoAutorizacionDto builderComprobanteRetencionV2(com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion retencionDos) {
        return CampoAutorizacionDto.builder()
                .numeroAutorizacion(retencionDos.getInfoTributaria().getClaveAcceso())
                .fechaAutorizacion(null)
                .comprobanteRetencionV2(retencionDos)
                .comprobante(XmlUtils.convertToXmlString(com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion.class, retencionDos))
                .build();
    }
}
