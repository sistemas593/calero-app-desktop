package com.calero.lili.api.modRRHH.modRRHHCabecera.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolCabeceraDto {

    private String numeroIdentificacion;
    private String tercero;
    private String tipoIdentificacion;
    private String codigoEstablecimiento;
    private String codigoResidencia;
    private String codigoPais;
    private String aplicaConvenio;
    private String tipoDiscapacidad;
    private String porcentajeDiscapacidad;
    private String idDiscapacidad;
    private String codigoSalario;
    private String fechaGeneracion;
    private BigDecimal ingresos;
    private BigDecimal comisiones;
    private BigDecimal ingresosNoGravadosIess;
    private BigDecimal decimoTercero;
    private BigDecimal decimoCuarto;
    private BigDecimal fondos;
    private BigDecimal utilidades;
    private BigDecimal ingresosNoGravadosOtrosEmpleadores;
    private BigDecimal iess;
    private BigDecimal gastosVivienda;
    private BigDecimal gastosSalud;
    private BigDecimal gastosAlimentacion;
    private BigDecimal gastosEducacion;
    private BigDecimal gastosVestimenta;
    private BigDecimal gastosTurismo;
    private BigDecimal exoneracionDiscapacidad;
    private BigDecimal exoneracionTerceraEdad;
    private BigDecimal asumidoOtrosEmpleadores;
    private BigDecimal baseImponible;
    private BigDecimal impuestoCausado;


}
