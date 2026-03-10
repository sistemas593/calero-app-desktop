package com.calero.lili.api.modCompras.modComprasImpuestos.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class FilterListDto {
    private String numeroAutorizacion;
    private String tipoDocumento;
    private String serie;
    private String secuencial;

    private String numeroIdentificacion;

    private String destino;

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

    private String fechaRegistroDesde;
    private String fechaRegistroHasta;

    public LocalDate getFechaRegistroDesde() {
        if(fechaRegistroDesde == null)
            return null;
        return DateUtils.toLocalDate(fechaRegistroDesde);
    }

    public LocalDate getFechaRegistroHasta() {
        if(fechaRegistroHasta == null)
            return null;
        return DateUtils.toLocalDate(fechaRegistroHasta);
    }

}
