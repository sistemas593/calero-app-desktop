package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FormaPagoSriEnum {


    SIN_UTILIZACION_DEL_SISTEMA_FINANCIERO("01", "SIN UTILIZACION DEL SISTEMA FINANCIERO"),
    COMPENSACION_DEUDAS("15", "COMPENSACIÓN DE DEUDAS"),
    TARJETA_DE_DEBITO("16", "TARJETA DE DÉBITO"),
    DINERO_ELECTRONICO("17", "DINERO ELECTRÓNICO"),
    TARJETA_PREPAGO("18", "TARJETA PREPAGO"),
    TARJETA_DE_CREDITO("19", "TARJETA DE CRÉDITO"),
    OTROS_CON_UTILIZACION_DEL_SISTEMA_FINANCIERO("20", "OTROS CON UTILIZACIÓN DEL SISTEMA FINANCIERO"),
    ENDOSO_DE_TITULOS("21", "ENDOSO DE TÍTULOS");


    private final String codigo;
    private final String nombre;


    public static String getNombrePago(String codigoPago) {

        for (FormaPagoSriEnum formaPago : FormaPagoSriEnum.values()) {
            if (formaPago.getCodigo().equals(codigoPago)) {
                return formaPago.getNombre();
            }
        }
        throw new IllegalArgumentException("El codigo de la forma de pago del SRI, no existe " + codigoPago);
    }

}
