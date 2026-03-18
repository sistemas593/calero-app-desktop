package com.calero.lili.core.utils.validaciones;

import com.calero.lili.core.errors.exceptions.GeneralException;

import java.lang.reflect.Field;

public  class ValidarCampoAscii {

    public static boolean contieneAsciiNoPermitido(String texto) {
        if (texto == null || texto.isEmpty()) {
            return false;
        }

        for (char c : texto.toCharArray()) {
            int ascii = (int) c;

            // Bloquea 1–8 y 10–32, permite 9
            if ((ascii >= 1 && ascii <= 8) || (ascii >= 10 && ascii <= 31)) {
                return true;
            }
        }
        return false;
    }

    public static void validarStrings(Object request) {
        if (request == null) return;

        for (Field field : request.getClass().getDeclaredFields()) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    String valor = (String) field.get(request);
                    if (ValidarCampoAscii.contieneAsciiNoPermitido(valor)) {
                        throw new GeneralException(
                                "El campo '" + field.getName() +
                                        "' contiene caracteres inválidos"
                        );
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error al validar campos", e);
                }
            }
        }
    }

}
