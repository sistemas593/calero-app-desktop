package com.calero.lili.core.comprobantes.builder.documentos.factura;

import com.calero.lili.core.comprobantes.builder.documentos.DetalleAdicionalBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.comprobantes.builder.documentos.ImpuestoBuilder;
import com.calero.lili.core.comprobantes.objetosXml.factura.Detalle;
import com.calero.lili.core.modVentas.VtVentaDetalleEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DetallesBuilder {

    private final ImpuestoBuilder impuestoBuilder;
    private final DetalleAdicionalBuilder detalleAdicionalBuilder;
    private final FormatoValores formatoValores;

    public List<Detalle> builderListDetalle(List<VtVentaDetalleEntity> list) {
        return list.stream()
                .map(this::builderDetalle)
                .toList();
    }

    private Detalle builderDetalle(VtVentaDetalleEntity detalle) {
        return Detalle.builder()
                .codigoPrincipal(detalle.getCodigoPrincipal())
                .codigoAuxiliar(detalle.getCodigoAuxiliar())
                .descripcion(detalle.getDescripcion())
                .cantidad(formatoValores.convertirBigDecimalToString(detalle.getCantidad()))
                .precioUnitario(formatoValores.convertirBigDecimalToString(detalle.getPrecioUnitario()))
                .descuento(formatoValores.convertirBigDecimalToString(detalle.getDescuento()))
                .precioTotalSinImpuesto(formatoValores.convertirBigDecimalToString(detalle.getSubtotalItem()))
                .impuesto(impuestoBuilder.builderListImpuesto(detalle.getImpuesto()))
                .detAdicional(detalleAdicionalBuilder.builderListDetalleAdicional(detalle.getDetAdicional()))
                .build();
    }

}
