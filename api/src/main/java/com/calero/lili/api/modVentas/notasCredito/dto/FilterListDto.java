package com.calero.lili.api.modVentas.notasCredito.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class FilterListDto {
    private Long idFactura;
    private String sucursal;
    private String tipoVenta;
    private String codigoDocumento;
    private String serie;
    private String secuencial;
    private String fechaEmisionDesde;
    private String fechaEmisionHasta;
    private String numeroIdentificacion;
    private String numeroAutorizacion;

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

    private Boolean anulada;
    private Boolean impresa;

}
