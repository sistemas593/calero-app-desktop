package com.calero.lili.core.enums;

public enum TipoAsiento {

    SI("Saldo Inicial"),
    DI("Diario"),
    CI("Comprobante de ingreso"),
    CE("Comprobante de egreso"),
    VT("VT");

    private final String descripcion;

    TipoAsiento(String descripcion) {
        this.descripcion = descripcion;
    }
}
