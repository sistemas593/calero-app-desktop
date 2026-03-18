package com.calero.lili.core.comprobantes.builder.documentos;

import com.calero.lili.core.comprobantes.objetosXml.factura.Pago;
import com.calero.lili.core.dtos.FormasPagoSri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FormaDePagoBuilder {
    private final FormatoValores formatoValores;

    public List<Pago> builderListFormaPagos(List<FormasPagoSri> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderFormaPago)
                .toList();
    }

    private Pago builderFormaPago(FormasPagoSri formasPagoSri) {
        return Pago.builder()
                .formaPago(formasPagoSri.getFormaPago())
                .total(formatoValores.convertirBigDecimalToString(formasPagoSri.getTotal()))
                .plazo(formasPagoSri.getPlazo())
                .unidadTiempo(formasPagoSri.getUnidadTiempo())
                .build();
    }

}
