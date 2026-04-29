package com.calero.lili.core.comprobantes.services.builder;

import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.services.dto.CampoAutorizacionDto;
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
}
