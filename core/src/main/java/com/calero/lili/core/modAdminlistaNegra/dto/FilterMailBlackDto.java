package com.calero.lili.core.modAdminlistaNegra.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Data
@ToString
public class FilterMailBlackDto {

    private String email;

    private String fechaDesde;

    private String fechaHasta;

    public LocalDate getFechaDesde() {
        if (Objects.isNull(fechaDesde)) return null;
        return DateUtils.toLocalDate(fechaDesde);
    }

    public LocalDate getFechaHasta() {
        if (Objects.isNull(fechaHasta)) return null;
        return DateUtils.toLocalDate(fechaHasta);
    }
}
