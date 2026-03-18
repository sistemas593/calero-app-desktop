package com.calero.lili.core.dtos;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class FilterFechasDto {

    private String fechaEmisionDesde;
    private String fechaEmisionHasta;


    public LocalDate getFechaEmisionDesde() {
        if(fechaEmisionDesde == null)
            return null;
        return DateUtils.toLocalDate(fechaEmisionDesde);
    }

    public LocalDate getFechaEmisionHasta() {
        if(fechaEmisionHasta == null)
            return null;
        return DateUtils.toLocalDate(fechaEmisionHasta);
    }

}
