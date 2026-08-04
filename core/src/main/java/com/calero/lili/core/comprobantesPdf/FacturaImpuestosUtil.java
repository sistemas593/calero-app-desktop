package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.TotalImpuesto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Calcula el desglose de IVA (0%, 5%, 8%, 15%, no objeto, exenta) de una
 * factura a partir de su lista de {@link TotalImpuesto}.
 * <p>
 * Se centraliza aquí porque tanto el PDF de hoja completa ({@link FacturaPdf},
 * A4/A5) como el de ticket ({@link TicktesFacturaServicePdf}, TICKET_58 /
 * TICKET_80) necesitan el mismo desglose, evitando repetir la búsqueda por
 * código de impuesto en cada clase.
 */
public final class FacturaImpuestosUtil {

    // Impuesto código 2 = IVA.
    // codigoPorcentaje -> 0: 0%, 4: 15%, 5: 5%, 8: 8%, 6: no objeto, 7: exenta
    private static final String CODIGO_IVA = "2";
    private static final String PORCENTAJE_0 = "0";
    private static final String PORCENTAJE_15 = "4";
    private static final String PORCENTAJE_8 = "8";
    private static final String PORCENTAJE_5 = "5";
    private static final String PORCENTAJE_NO_OBJETO = "6";
    private static final String PORCENTAJE_EXENTA = "7";

    private FacturaImpuestosUtil() {
    }

    public static SubtotalesFactura resolverSubtotales(List<TotalImpuesto> totalImpuestos) {
        if (totalImpuestos == null || totalImpuestos.isEmpty()) {
            return new SubtotalesFactura(null, null, null, null, null, null);
        }

        return new SubtotalesFactura(
                buscar(totalImpuestos, PORCENTAJE_0),
                buscar(totalImpuestos, PORCENTAJE_15),
                buscar(totalImpuestos, PORCENTAJE_8),
                buscar(totalImpuestos, PORCENTAJE_5),
                buscar(totalImpuestos, PORCENTAJE_NO_OBJETO),
                buscar(totalImpuestos, PORCENTAJE_EXENTA));
    }

    private static TotalImpuesto buscar(List<TotalImpuesto> totalImpuestos, String codigoPorcentaje) {
        return totalImpuestos.stream()
                .filter(imp -> CODIGO_IVA.equals(imp.getCodigo()) && codigoPorcentaje.equals(imp.getCodigoPorcentaje()))
                .findAny()
                .orElse(null);
    }

    @Getter
    @AllArgsConstructor
    public static class SubtotalesFactura {
        private final TotalImpuesto subTotal0;
        private final TotalImpuesto subTotal15;
        private final TotalImpuesto subTotal8;
        private final TotalImpuesto subTotal5;
        private final TotalImpuesto subTotalNoObjeto;
        private final TotalImpuesto subTotalExenta;
    }
}
