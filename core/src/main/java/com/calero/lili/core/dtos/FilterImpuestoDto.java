package com.calero.lili.core.dtos;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class FilterImpuestoDto {

    public String fechaDesde;
    public String fechaHasta;
    public String periodo;


    public LocalDate getFechaDesde() {
        if (Objects.isNull(fechaDesde)) return null;
        return DateUtils.toLocalDate(fechaDesde);
    }

    public LocalDate getFechaHasta() {
        if (Objects.isNull(fechaHasta)) return null;
        return DateUtils.toLocalDate(fechaHasta);
    }
}
