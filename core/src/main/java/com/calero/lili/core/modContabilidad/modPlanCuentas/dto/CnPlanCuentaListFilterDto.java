package com.calero.lili.core.modContabilidad.modPlanCuentas.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Data
@ToString
public class CnPlanCuentaListFilterDto {

    private String filter;
    private String codigoCuenta;
    private String codigoCuentaOriginal;
    private String cuenta;
    private Boolean mayor;
    private Integer nivel;

    private String fechaEmisionDesde;
    private String fechaEmisionHasta;
    private String cuentaInicial;
    private String cuentaFinal;
    private String sucursal;
    private Boolean positivo;
    private String codigoCentroCostos;

    public Boolean getPositivo() {
        if (Objects.isNull(positivo)) return Boolean.TRUE;
        return positivo;
    }

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

    public Boolean getMayor() {
        if (Objects.isNull(mayor)) return Boolean.FALSE;
        return mayor;
    }

    public String getCuentaInicial() {
        if (Objects.isNull(cuentaInicial)) return null;
        if (cuentaInicial.isEmpty()) return null;
        return cuentaInicial;
    }

    public String getCuentaFinal() {
        if (Objects.isNull(cuentaFinal)) return null;
        if (cuentaFinal.isEmpty()) return null;
        return cuentaFinal;
    }

    public String getCodigoCentroCostos() {
        if (Objects.isNull(codigoCentroCostos)) return null;
        if (codigoCentroCostos.isEmpty()) return null;
        return codigoCentroCostos;
    }


}
