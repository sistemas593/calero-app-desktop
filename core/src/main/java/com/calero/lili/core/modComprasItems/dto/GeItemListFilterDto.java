package com.calero.lili.core.modComprasItems.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class GeItemListFilterDto {

    private String filter;
    private String fechaTarifaIva;


    public LocalDate getFechaTarifaIva() {
        if (fechaTarifaIva == null)
            return null;
        return DateUtils.toLocalDate(fechaTarifaIva);
    }
}
