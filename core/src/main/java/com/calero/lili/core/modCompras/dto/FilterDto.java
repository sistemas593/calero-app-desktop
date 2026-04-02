package com.calero.lili.core.modCompras.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class FilterDto {

    private String fechaRegistroDesde;
    private String fechaRegistroHasta;


    public LocalDate getFechaRegistroHasta() {
        if (fechaRegistroHasta == null)
            return null;
        return DateUtils.toLocalDate(fechaRegistroHasta);
    }

    public LocalDate getFechaRegistroDesde() {
        if (fechaRegistroDesde == null)
            return null;
        return DateUtils.toLocalDate(fechaRegistroDesde);
    }
}
