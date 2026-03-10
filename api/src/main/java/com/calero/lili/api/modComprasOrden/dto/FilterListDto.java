package com.calero.lili.api.modComprasOrden.dto;

import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class FilterListDto {
    private Long idFactura;
    private String sucursal;
    private TipoVenta tipoVenta;
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
