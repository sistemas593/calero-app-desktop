package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CodigoDocumento {
    FACTURA("01","FACTURA"),
    NOTA_VENTA("02","NOTA DE VENTA"),
    LIQUIDACION("03","LIQUIDACION"),
    NOTA_CREDITO("04","NOTA DE CREDITO"),
    NOTA_DEBITO("05","NOTA DE DEBITO"),
    COMPROBANTE_RETENCION("07","COMPROBANTE DE RETENCION"),
    OTROS("18","OTROS AUT VENTAS");

    private final String codigoDocumento;
    private final String nombreDocumento;

    public static CodigoDocumento fromCodigo(String codigo) throws Exception {
        for (CodigoDocumento nombre : CodigoDocumento.values()) {
            if (nombre.getCodigoDocumento().equals(codigo)) {
                return nombre;
            }
        }
        throw new Exception("Código documento no válido: " + codigo);
    }

}
