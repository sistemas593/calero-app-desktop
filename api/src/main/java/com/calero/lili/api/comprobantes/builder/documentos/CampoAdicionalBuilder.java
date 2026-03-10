package com.calero.lili.api.comprobantes.builder.documentos;

import com.calero.lili.api.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.core.dtos.InformacionAdicional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CampoAdicionalBuilder {

    public List<CampoAdicional> builderListCampoAdicional(List<InformacionAdicional> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderInformacionAdicional)
                .toList();
    }

    private CampoAdicional builderInformacionAdicional(InformacionAdicional informacionAdicional) {
        return CampoAdicional.builder()
                .nombre(informacionAdicional.getNombre())
                .valor(informacionAdicional.getValor())
                .build();
    }

}
