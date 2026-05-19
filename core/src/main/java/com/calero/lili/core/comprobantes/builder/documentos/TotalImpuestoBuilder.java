package com.calero.lili.core.comprobantes.builder.documentos;

import com.calero.lili.core.comprobantes.objetosXml.TotalImpuesto;
import com.calero.lili.core.modVentas.VtVentaValoresEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TotalImpuestoBuilder {
    private final FormatoValores formatoValores;

    public List<TotalImpuesto> builderListTotalImpuesto(List<VtVentaValoresEntity> list) {
        if(Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderTotalImpuesto)
                .toList();
    }

    private TotalImpuesto builderTotalImpuesto(VtVentaValoresEntity model) {
        return TotalImpuesto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(formatoValores.convertirBigDecimalToString(model.getBaseImponible()))
                .valor(formatoValores.convertirBigDecimalToString(model.getValor()))
                .build();
    }

}
