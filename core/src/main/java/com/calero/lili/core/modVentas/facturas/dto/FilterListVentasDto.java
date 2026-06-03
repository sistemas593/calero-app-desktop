package com.calero.lili.core.modVentas.facturas.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
public class FilterListVentasDto {


    private String sucursal;
    private String tipoVenta;
    private String serie;
    private String secuencial;
    private String fechaEmisionDesde;
    private String fechaEmisionHasta;
    private UUID idTercero;
    private String numeroIdentificacion;
    private String terceroNombre;
    private String numeroAutorizacion;

    public LocalDateTime getFechaEmisionDesde() {
        if (fechaEmisionDesde == null)
            return null;
        return DateUtils.toLocalDateTimeFechaDesde(fechaEmisionDesde);
    }

    public LocalDateTime getFechaEmisionHasta() {
        if (fechaEmisionHasta == null)
            return null;
        return DateUtils.toLocalDateTimeFechaHasta(fechaEmisionHasta);
    }

    private Boolean anulada;
    private Boolean impresa;

}
