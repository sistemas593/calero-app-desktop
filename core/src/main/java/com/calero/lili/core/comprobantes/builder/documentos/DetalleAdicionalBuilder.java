package com.calero.lili.core.comprobantes.builder.documentos;

import com.calero.lili.core.comprobantes.objetosXml.factura.DetAdicional;
import com.calero.lili.core.modVentas.VtVentaDetalleEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class DetalleAdicionalBuilder {

    public List<DetAdicional> builderListDetalleAdicional(List<VtVentaDetalleEntity.DetalleAdicional> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetalleAdicional)
                .toList();
    }

    private DetAdicional builderDetalleAdicional(VtVentaDetalleEntity.DetalleAdicional detalleAdicional) {
        return DetAdicional.builder()
                .nombre(detalleAdicional.getNombre())
                .valor(detalleAdicional.getValor())
                .build();
    }

}
