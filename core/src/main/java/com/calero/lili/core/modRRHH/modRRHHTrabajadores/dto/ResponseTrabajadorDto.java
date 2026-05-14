package com.calero.lili.core.modRRHH.modRRHHTrabajadores.dto;

import com.calero.lili.core.modLocalidades.modCantones.dto.ResponseCantonDto;
import com.calero.lili.core.modLocalidades.modProvincias.dto.ResponseProvinciaDto;
import com.calero.lili.core.tablas.tbPaises.TbPaisGetOneDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTrabajadorDto {

    private UUID idTrabajador;
    private String codigoSalario;
    private String codigoEstablecimiento;
    private String codigoResidencia;
    private String aplicaConvenio;
    private String tipoDiscapacidad;
    private Integer porcentajeDiscapacidad;
    private String tipoIdDiscapacidad;
    private String beneficioProvGalapagos;
    private String enfermedadCatastrofica;
    private String fechaIngreso;
    private Integer estado;
    private String fechaNacimiento;
    private String fechaSalida;
    private UUID idCargo;
    private BigDecimal sueldoBase;
    private ResponseProvinciaDto provincia;
    private ResponseCantonDto canton;
    private String apellidos;
    private String nombres;

}
