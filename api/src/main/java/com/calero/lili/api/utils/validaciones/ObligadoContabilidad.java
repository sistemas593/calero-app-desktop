package com.calero.lili.api.utils.validaciones;

public class ObligadoContabilidad {

    public static String getObligadoContabilidad(String obligadoContabilidad) {
        if (obligadoContabilidad.equals("S")) {
            return "SI";
        } else {
            return "NO";
        }
    }

}
