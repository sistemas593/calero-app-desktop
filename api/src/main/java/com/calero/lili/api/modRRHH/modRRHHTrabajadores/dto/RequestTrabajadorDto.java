package com.calero.lili.api.modRRHH.modRRHHTrabajadores.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class RequestTrabajadorDto {

    private String codigoProvincia;
    private String codigoCanton;
    private String codigoPais;
    private String codigoSalario;
    private String codigoEstablecimiento;
    private String codigoResidencia;
    private String aplicaConvenio;
    private String tipoDiscapacidad;
    private Integer porcentajeDiscapacidad;
    private String tipoIdDiscapacidad;
    private String beneficioProvGalapagos;
    private String enfermedadCatastrofica;
    private String idDiscapacidad;
    private String fechaIngreso;
    private Integer estado;
    private String fechaNacimiento;
    private String fechaSalida;
    private UUID idCargo;
    private BigDecimal sueldoBase;
    private String apellidos;
    private String nombres;

}
