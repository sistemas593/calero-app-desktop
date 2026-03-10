package com.calero.lili.api.modContabilidad.modAsientos.dto;

import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

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
    private String codigoCuenta;
    private String codigoCentroCostos;


    public LocalDate getFechaEmisionDesde() {
        if (fechaEmisionDesde == null)
            return null;
        return DateUtils.toLocalDate(fechaEmisionDesde);
    }

    public LocalDate getFechaEmisionHasta() {
        if (fechaEmisionHasta == null)
            return null;
        return DateUtils.toLocalDate(fechaEmisionHasta);
    }

    public String getCodigoCentroCostos() {
        if (Objects.nonNull(codigoCentroCostos)) {
            if (codigoCentroCostos.isEmpty()) return null;
        }
        return codigoCentroCostos;
    }

    private Boolean anulada;
    private Boolean impresa;

}
