package com.calero.lili.api.modCompras.modComprasImpuestos.dto;

import com.calero.lili.api.modCompras.modComprasImpuestos.projection.ComprasImpuestoProjection;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AcumulacionProveedorTotalesDto {

    private BigDecimal baseCero = BigDecimal.ZERO;
    private BigDecimal base15 = BigDecimal.ZERO;
    private BigDecimal iva15 = BigDecimal.ZERO;
    private BigDecimal base5 = BigDecimal.ZERO;
    private BigDecimal iva5 = BigDecimal.ZERO;
    private BigDecimal base8 = BigDecimal.ZERO;
    private BigDecimal iva8 = BigDecimal.ZERO;
    private BigDecimal noObjeto = BigDecimal.ZERO;
    private BigDecimal exenta = BigDecimal.ZERO;

    public void acumularPorProveedor(ComprasImpuestoProjection f) {
        baseCero = baseCero.add(f.getBase_imponible_cero());
        base15 = base15.add(f.getBase_imponible_quince());
        iva15 = iva15.add(f.getValor_iva_quince());
        base5 = base5.add(f.getBase_imponible_cinco());
        iva5 = iva5.add(f.getValor_iva_cinco());
        base8 = base8.add(f.getBase_imponible_ocho());
        iva8 = iva8.add(f.getValor_iva_ocho());
        noObjeto = noObjeto.add(f.getBase_imponible_no_objecto());
        exenta = exenta.add(f.getBase_imponible_exento());
    }

}
