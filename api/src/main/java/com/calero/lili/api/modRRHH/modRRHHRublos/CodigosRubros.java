package com.calero.lili.api.modRRHH.modRRHHRublos;

import java.util.List;

public abstract class CodigosRubros {

    public CodigosRubros() {
    }

    public static final String GASTOS_PERSONALES_VIVIENDA = "GTP-001";
    public static final String GASTOS_PERSONALES_SALUD = "GTP-002";
    public static final String GASTOS_PERSONALES_ALIMENTACION = "GTP-003";
    public static final String GASTOS_PERSONALES_EDUCACION = "GTP-004";
    public static final String GASTOS_PERSONALES_VESTIMENTA = "GTP-005";
    public static final String GASTOS_PERSONALES_TURISMO = "GTP-006";
    public static final String EXONERACION_DISCAPACIDAD = "EXO-001";
    public static final String EXONERACION_TERCERA_EDAD = "EXO-002";


    public static final List<String> TODOS_LOS_CODIGOS = List.of(
            GASTOS_PERSONALES_VIVIENDA,
            GASTOS_PERSONALES_SALUD,
            GASTOS_PERSONALES_ALIMENTACION,
            GASTOS_PERSONALES_EDUCACION,
            GASTOS_PERSONALES_VESTIMENTA,
            GASTOS_PERSONALES_TURISMO,
            EXONERACION_DISCAPACIDAD,
            EXONERACION_TERCERA_EDAD
    );

}
