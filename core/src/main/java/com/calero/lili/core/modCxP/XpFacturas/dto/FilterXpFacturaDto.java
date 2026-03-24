package com.calero.lili.core.modCxP.XpFacturas.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterXpFacturaDto {

    private String fechaRegistroDesde;
    private String fechaRegistroHasta;


    public LocalDate getFechaRegistroDesde() {
        if (Objects.isNull(fechaRegistroDesde)) {
            return null;
        }
        return DateUtils.toLocalDate(fechaRegistroDesde);
    }

    public LocalDate getFechaRegistroHasta() {
        if (Objects.isNull(fechaRegistroHasta)) {
            return null;
        }
        return DateUtils.toLocalDate(fechaRegistroHasta);
    }

    private UUID idTercero;
    private String sucursal;
}
