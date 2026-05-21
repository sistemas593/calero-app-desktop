package com.calero.lili.core.utils;

import com.calero.lili.core.errors.exceptions.GeneralException;

public class ValidacionDocumentosGeneral {


    public static void validarSizeSecuencial(String secuencial) {
        if (secuencial.length() != 9) {
            throw new GeneralException("El secuencial debe ser solo de 9 dígitos");
        }
    }

}
