package com.calero.lili.api.modRRHH.modRRHHCabecera.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolCabeceraPDFDto {

    private String numeroIdentificacion;
    private String trabajador;
    private String sueldo;
    private String sobreSueldos;
    private String otros;
    private String decimoTercero;
    private String decimoCuarto;
    private String fondos;
    private String utilidades;
    private String iessPersonal;
    private String gastosPersonales;
    private String terceraEdadYDiscapacitados;
    private String ingresosOtrosEmpleadores;
    private String iessOtrosEmpleadores;
    private String baseImponible;
    private String retenidoEsteEmpleador;
    private String retenidoOtrosEmpleador;
    private String totalSueldo;
    private String totalSobreSueldos;
    private String totalOtros;
    private String totalDecimoTercero;
    private String totalDecimoCuarto;
    private String totalFondos;
    private String totalUtilidades;
    private String totalIessPersonal;
    private String totalGastosPersonales;
    private String totalTerceraEdadYDiscapacitados;
    private String totalIngresosOtrosEmpleadores;
    private String totalIessOtrosEmpleadores;
    private String totalBaseImponible;
    private String totalRetenidoEsteEmpleador;
    private String totalRetenidoOtrosEmpleador;

}
