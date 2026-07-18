package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum DocumentoEnum {


    D01("Factura", "01"),
    D20("Retencion", "20"),
    D04("Nota Credito", "04"),
    D05("Nota Debito", "05"),
    D06("Guia Remision", "06"),
    D07("Comprobante Retencion", "07"),
    D41("Reembolso", "41");


    public static DocumentoEnum getCodigoDocumento(String codigoDocumento) {
        if (Objects.isNull(codigoDocumento) || codigoDocumento.isEmpty()) {
            return D01;
        }


        for (DocumentoEnum documento : DocumentoEnum.values()) {
            if (documento.getCodigo().equals(codigoDocumento)) {
                return documento;
            }
        }
        throw new IllegalArgumentException("Código de documento no válido: " + codigoDocumento);


    }


    private final String nombre;
    private final String codigo;
}
