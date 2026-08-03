package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum TipoDocumentoPdf {

    FAC("FACTURA", "01"),
    NDB("NDB", "05"),
    NCR("NCR", "04"),
    GRM("GRM", "06"),
    LIQ("LIQ", "03"),
    CRT("CRT", "07");

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
