package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum TipoDocumentoPdf {

    FAC("FACTURA", "01"),
    NDB("NOTA DE DÉBITO", "05"),
    NCR("NOTA DE CRÉDITO", "04"),
    GRM("GUIA DE REMISIÓN", "06"),
    LIQ("LIQUIDACIÓN COMPRA", "03"),
    CRT("COMPROBANTE DE RETENCIÓN", "07");

    private final String nombre;
    private final String codigo;


    public static TipoDocumentoPdf getTipoDocumento(String codigo) {
        if (Objects.isNull(codigo) || codigo.isEmpty()) {
            return FAC;
        }

        for (TipoDocumentoPdf documento : TipoDocumentoPdf.values()) {
            if (documento.getCodigo().equals(codigo)) {
                return documento;
            }
        }
        throw new IllegalArgumentException("El tipo de documento no válido: " + codigo);

    }

}
