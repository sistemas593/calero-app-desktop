package com.calero.lili.core.modTesoreria.TsComprobanteIngreso.dto;

import com.calero.lili.core.enums.TipoAsiento;
import com.calero.lili.core.utils.DateUtils;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class ComprobanteIngresoFilterDto {


    private String fechaComprobanteDesde;
    private String fechaComprobanteHasta;
    private String sucursal;
    private TipoAsiento tipoAsiento;
    private String numeroAsientoDesde;
    private String numeroAsientoHasta;


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
