package com.calero.lili.core.modClientesConfiguraciones.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Data
@ToString
public class VtClientesConfiguracionesListFilterDto {

    private String filter;

    private String fechaVencimientoDesde;
    private String fechaVencimientoHasta;
    private String fechaBloqueoDesde;
    private String fechaBloqueoHasta;


    public String getFilter() {
        if (Objects.isNull(filter) || filter.isEmpty()) return null;
        return filter;
    }

    public LocalDate getFechaVencimientoDesde() {
        if (Objects.isNull(fechaVencimientoDesde) || fechaVencimientoDesde.isEmpty()) return null;
        return DateUtils.toLocalDate(fechaVencimientoDesde);
    }

    public LocalDate getFechaVencimientoHasta() {

        if (Objects.isNull(fechaVencimientoHasta) || fechaVencimientoHasta.isEmpty()) return null;
        return DateUtils.toLocalDate(fechaVencimientoHasta);
    }

    public LocalDate getFechaBloqueoDesde() {
        if (Objects.isNull(fechaBloqueoDesde) || fechaBloqueoDesde.isEmpty()) return null;
        return DateUtils.toLocalDate(fechaBloqueoDesde);
    }

    public LocalDate getFechaBloqueoHasta() {
        if (Objects.isNull(fechaBloqueoHasta) || fechaBloqueoHasta.isEmpty()) return null;
        return DateUtils.toLocalDate(fechaBloqueoHasta);
    }
}
