package com.calero.lili.api.modRRHH.modRRHHTrabajadores.dto;

import com.calero.lili.api.modLocalidades.modCantones.dto.ResponseCantonDto;
import com.calero.lili.api.modLocalidades.modCantones.dto.ResponseProvinciaDto;
import com.calero.lili.api.tablas.tbPaises.TbPaisGetOneDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
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
    private ResponseCantonDto cantonDto;
    private TbPaisGetOneDto pais;
    private String apellidos;
    private String nombres;

}
