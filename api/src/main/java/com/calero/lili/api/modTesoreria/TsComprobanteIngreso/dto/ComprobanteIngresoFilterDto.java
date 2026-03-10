package com.calero.lili.api.modTesoreria.TsComprobanteIngreso.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class ComprobanteIngresoFilterDto {


    private String fechaComprobanteDesde;
    private String fechaComprobanteHasta;
    private String sucursal;


    public LocalDate getFechaComprobanteDesde() {
        if (Objects.isNull(fechaComprobanteDesde)) {
            return null;
        }
        return DateUtils.toLocalDate(fechaComprobanteDesde);
    }

    public LocalDate getFechaComprobanteHasta() {
        if (Objects.isNull(fechaComprobanteHasta)) {
            return null;
        }
        return DateUtils.toLocalDate(fechaComprobanteHasta);
    }
}
