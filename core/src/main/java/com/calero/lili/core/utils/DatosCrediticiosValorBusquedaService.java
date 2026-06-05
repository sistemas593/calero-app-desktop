package com.calero.lili.core.utils;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DatosCrediticiosValorBusquedaService {

    // AQUI SE OBTIENE EL VALOR DEL REPORTE DE DATOS CREDITICIOS PARA SU BUSQUEDA DEPENDIENDO DEL AÑO

    private final BigDecimal valor2026 = new BigDecimal("482");

    public BigDecimal obtenerValorAnual(String periodo) {
        LocalDate fecha = DateUtils.toPeriodoDate(periodo);
        int year = fecha.getYear();

        if (year == 2026) {
            BigDecimal x = valor2026.multiply(new BigDecimal("15.00"));
            return x.divide(new BigDecimal("100.00"));
        }

        return BigDecimal.ZERO;
    }


}
