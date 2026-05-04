package com.calero.lili.core.comprobantes.services.builder;

import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.comprobantes.services.dto.CampoAutorizacionDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class CampoAutorizacionBuilder {


    LocalDate date = LocalDate.of(2000, 1, 1);
    LocalDateTime fecha = date.atStartOfDay();

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
                .fechaAutorizacion(DateUtils.toLocalDateTimeString(fecha))
                .factura(documento)
                .build();
    }

    public CampoAutorizacionDto builderNotaCredito(NotaCredito documento) {
        return CampoAutorizacionDto.builder()
                .numeroAutorizacion(documento.getInfoTributaria().getClaveAcceso())
                .fechaAutorizacion(DateUtils.toLocalDateTimeString(fecha))
                .notaCredito(documento)
                .build();
    }

    public CampoAutorizacionDto builderNotaDebito(NotaDebito documento) {
        return CampoAutorizacionDto.builder()
                .numeroAutorizacion(documento.getInfoTributaria().getClaveAcceso())
                .fechaAutorizacion(DateUtils.toLocalDateTimeString(fecha))
                .notaDebito(documento)
                .build();
    }

    public CampoAutorizacionDto builderComprobanteRetencionV1(ComprobanteRetencion retencionUno) {
        return CampoAutorizacionDto.builder()
                .numeroAutorizacion(retencionUno.getInfoTributaria().getClaveAcceso())
                .fechaAutorizacion(DateUtils.toLocalDateTimeString(fecha))
                .comprobanteRetencionV1(retencionUno)
                .build();
    }

    public CampoAutorizacionDto builderComprobanteRetencionV2(com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion retencionDos) {
        return CampoAutorizacionDto.builder()
                .numeroAutorizacion(retencionDos.getInfoTributaria().getClaveAcceso())
                .fechaAutorizacion(DateUtils.toLocalDateTimeString(fecha))
                .comprobanteRetencionV2(retencionDos)
                .build();
    }
}
