package com.calero.lili.api.modAdminPorcentajes.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class FilterListDto {

    private String fechaDesde;
    private String fechaHasta;


    public LocalDate getFechaDesde() {
        if (fechaDesde == null)
            return null;
        return DateUtils.toLocalDate(fechaDesde);
    }

    public LocalDate getFechaHasta() {
        if (fechaHasta == null)
            return null;
        return DateUtils.toLocalDate(fechaHasta);
    }


}
