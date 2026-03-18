package com.calero.lili.core.comprobantes.builder.documentos;

import com.calero.lili.core.comprobantes.objetosXml.factura.Impuesto;
import com.calero.lili.core.modVentas.VtVentaDetalleEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ImpuestoBuilder {

    private final FormatoValores formatoValores;
    public List<Impuesto> builderListImpuesto(List<VtVentaDetalleEntity.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuesto)
                .toList();
    }

    private Impuesto builderImpuesto(VtVentaDetalleEntity.Impuestos impuesto) {
        return Impuesto.builder()
                .codigo(impuesto.getCodigo())
                .codigoPorcentaje(impuesto.getCodigoPorcentaje())
                .tarifa(formatoValores.convertirTarifaToString(impuesto.getTarifa()))
                .baseImponible(formatoValores.convertirBigDecimalToString(impuesto.getBaseImponible()))
                .valor(formatoValores.convertirBigDecimalToString(impuesto.getValor()))
                .build();
    }
}
