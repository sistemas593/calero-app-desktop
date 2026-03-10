package com.calero.lili.api.modRRHH.modRRHHCabecera.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Data
@ToString
public class FilterRolCabeceraDto {

    private String anio;
    private String mes;
    private UUID idTercero;
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

    public UUID getIdTercero() {
        if (Objects.nonNull(idTercero)) {
            if (idTercero.toString().isEmpty()) return null;
        }
        return idTercero;
    }

    // no deben ser negativos y solo deben tener dos decimales.
}
