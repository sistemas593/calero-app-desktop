package com.calero.lili.api.comprobantes.builder.documentos.liquidaciones;

import com.calero.lili.api.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.api.comprobantes.objetosXml.factura.TotalImpuesto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.CpLiquidacionesValoresEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TotalImpuestoLiquidacionBuilder {

    private final FormatoValores formatoValores;

    public List<TotalImpuesto> builderListTotalImpuesto(List<CpLiquidacionesValoresEntity> list) {
        return list.stream()
                .map(this::builderTotalImpuesto)
                .toList();
    }

    private TotalImpuesto builderTotalImpuesto(CpLiquidacionesValoresEntity model) {
        return TotalImpuesto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(formatoValores.convertirBigDecimalToString(model.getBaseImponible()))
                .valor(formatoValores.convertirBigDecimalToString(model.getValor()))
                .tarifa(formatoValores.convertirTarifaToString(model.getTarifa()))
                .build();
    }

}
