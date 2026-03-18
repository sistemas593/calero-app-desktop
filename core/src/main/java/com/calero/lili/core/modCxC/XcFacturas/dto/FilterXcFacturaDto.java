package com.calero.lili.core.modCxC.XcFacturas.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
public class FilterXcFacturaDto {

    private String sucursal;
    private String fechaEmisionDesde;
    private String fechaEmisionHasta;


    public LocalDate getFechaEmisionDesde() {
        if (Objects.isNull(fechaEmisionDesde)) {
            return null;
        }
        return DateUtils.toLocalDate(fechaEmisionDesde);
    }

    public LocalDate getFechaEmisionHasta() {
        if (Objects.isNull(fechaEmisionHasta)) {
            return null;
        }
        return DateUtils.toLocalDate(fechaEmisionHasta);
    }

    private UUID idTercero;
}
