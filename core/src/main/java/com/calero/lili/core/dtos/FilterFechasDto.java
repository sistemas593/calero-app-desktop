package com.calero.lili.core.dtos;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Data
@ToString
public class FilterFechasDto {

    private String fechaEmisionDesde;
    private String fechaEmisionHasta;

    private String fechaDesde;
    private String fechaHasta;
    private String tipo;

    public LocalDate getFechaEmisionDesde() {
        if (fechaEmisionDesde == null)
            return null;
        return DateUtils.toLocalDate(fechaEmisionDesde);
    }

    public LocalDate getFechaEmisionHasta() {
        if (fechaEmisionHasta == null)
            return null;
        return DateUtils.toLocalDate(fechaEmisionHasta);
    }


    public LocalDate getFechaDesde() {
        if (fechaDesde == null) return null;
        return DateUtils.toLocalDate(fechaDesde);

    }

    public LocalDate getFechaHasta() {
        if (fechaHasta == null) return null;
        return DateUtils.toLocalDate(fechaHasta);
    }

    public String getTipo() {
        if (Objects.isNull(tipo) || tipo.isEmpty()) {
            return null;
        }

        String x = tipo.toLowerCase();

        if (x.equals("informativo")) {
            return "I";
        }

        if (x.equals("error")) {
            return "E";
        }

        return tipo;
    }
}
