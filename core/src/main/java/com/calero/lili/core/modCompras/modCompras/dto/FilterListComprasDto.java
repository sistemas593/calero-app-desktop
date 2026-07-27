package com.calero.lili.core.modCompras.modCompras.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString
public class FilterListComprasDto {

    private String sucursal;
    private String serie;
    private String secuencial;
    private String fechaEmisionDesde;
    private String fechaEmisionHasta;
    private String numeroIdentificacion;
    private UUID idTercero;


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
