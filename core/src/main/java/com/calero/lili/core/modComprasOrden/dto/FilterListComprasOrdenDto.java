package com.calero.lili.core.modComprasOrden.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class FilterListComprasOrdenDto {


    private String sucursal;
    private String secuencial;
    private String fechaEmisionDesde;
    private String fechaEmisionHasta;

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

    private Boolean anulada;
    private Boolean impresa;

}
