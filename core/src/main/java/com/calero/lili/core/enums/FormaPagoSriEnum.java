package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum FormaPagoSriEnum {


    F01("01", "SIN UTILIZACION DEL SISTEMA FINANCIERO"),
    F15("15", "COMPENSACIÓN DE DEUDAS"),
    F16("16", "TARJETA DE DÉBITO"),
    F17("17", "DINERO ELECTRÓNICO"),
    F18("18", "TARJETA PREPAGO"),
    F19("19", "TARJETA DE CRÉDITO"),
    F20("20", "OTROS CON UTILIZACIÓN DEL SISTEMA FINANCIERO"),
    F21("21", "ENDOSO DE TÍTULOS");


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

    public static FormaPagoSriEnum getFormaPagoSri(String codigoPago) {

        if (Objects.isNull(codigoPago) || codigoPago.isEmpty()) {
            return FormaPagoSriEnum.F01;
        }
        
        for (FormaPagoSriEnum formaPago : FormaPagoSriEnum.values()) {
            if (formaPago.getCodigo().equals(codigoPago)) {
                return formaPago;
            }
        }
        throw new IllegalArgumentException("El codigo de la forma de pago del SRI, no existe " + codigoPago);
    }

}
