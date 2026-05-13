package com.calero.lili.core.modRRHH.modRRHHTrabajadores.builder;

import com.calero.lili.core.modRRHH.modRRHHTrabajadores.TrabajadorEntity;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.dto.RequestTrabajadorDto;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.dto.ResponseTrabajadorDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisGetOneDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TrabajadorBuilder {


    public TrabajadorEntity builderEntity(RequestTrabajadorDto model, GeTerceroEntity tercero) {
        return TrabajadorEntity.builder()
                .idTrabajador(tercero.getIdTercero())
                .codigoSalario(model.getCodigoSalario())
                .codigoEstablecimiento(model.getCodigoEstablecimiento())
                .codigoResidencia(model.getCodigoResidencia())
                .aplicaConvenio(model.getAplicaConvenio())
                .tipoDiscapacidad(model.getTipoDiscapacidad())
                .porcentajeDiscapacidad(model.getPorcentajeDiscapacidad())
                .tipoIdDiscapacidad(model.getTipoIdDiscapacidad())
                .beneficioProvGalapagos(model.getBeneficioProvGalapagos())
                .enfermedadCatastrofica(model.getEnfermedadCatastrofica())
                .fechaIngreso(Objects.nonNull(model.getFechaIngreso()) && !model.getFechaIngreso().isEmpty()
                        ? DateUtils.toLocalDate(model.getFechaIngreso())
                        : null)
                .estado(model.getEstado())
                .idDiscapacidad(model.getIdDiscapacidad())
                .pais(builderPais(model.getCodigoPais()))
                .tercero(tercero)
                .estado(model.getEstado())
                .sueldoBase(model.getSueldoBase())
                .fechaNacimiento(Objects.nonNull(model.getFechaNacimiento()) && !model.getFechaNacimiento().isEmpty()
                        ? DateUtils.toLocalDate(model.getFechaNacimiento()) : null)
                .fechaSalida(Objects.nonNull(model.getFechaSalida()) && !model.getFechaSalida().isEmpty()
                        ? DateUtils.toLocalDate(model.getFechaSalida()) : null)
                .idCargo(model.getIdCargo())
                .apellidos(model.getApellidos())
                .nombres(model.getNombres())
                .build();
    }

    public TrabajadorEntity builderUpdateEntity(RequestTrabajadorDto model, TrabajadorEntity item, GeTerceroEntity tercero) {
        return TrabajadorEntity.builder()
                .idTrabajador(item.getIdTrabajador())
                .codigoSalario(model.getCodigoSalario())
                .codigoEstablecimiento(model.getCodigoEstablecimiento())
                .codigoResidencia(model.getCodigoResidencia())
                .aplicaConvenio(model.getAplicaConvenio())
                .tipoDiscapacidad(model.getTipoDiscapacidad())
                .porcentajeDiscapacidad(model.getPorcentajeDiscapacidad())
                .tipoIdDiscapacidad(model.getTipoIdDiscapacidad())
                .beneficioProvGalapagos(model.getBeneficioProvGalapagos())
                .enfermedadCatastrofica(model.getEnfermedadCatastrofica())
                .fechaIngreso(DateUtils.toLocalDate(model.getFechaIngreso()))
                .estado(model.getEstado())
                .idDiscapacidad(model.getIdDiscapacidad())
                .pais(builderPais(model.getCodigoPais()))
                .tercero(tercero)
                .estado(model.getEstado())
                .sueldoBase(model.getSueldoBase())
                .fechaNacimiento(Objects.nonNull(model.getFechaNacimiento())
                        ? DateUtils.toLocalDate(model.getFechaNacimiento()) : null)
                .fechaSalida(Objects.nonNull(model.getFechaSalida())
                        ? DateUtils.toLocalDate(model.getFechaSalida()) : null)
                .idCargo(model.getIdCargo())
                .apellidos(model.getApellidos())
                .nombres(model.getNombres())
                .build();
    }

    public ResponseTrabajadorDto builderResponse(TrabajadorEntity model) {
        return ResponseTrabajadorDto.builder()
                .idTrabajador(model.getIdTrabajador())
                .codigoSalario(model.getCodigoSalario())
                .codigoEstablecimiento(model.getCodigoEstablecimiento())
                .codigoResidencia(model.getCodigoResidencia())
                .aplicaConvenio(model.getAplicaConvenio())
                .tipoDiscapacidad(model.getTipoDiscapacidad())
                .porcentajeDiscapacidad(model.getPorcentajeDiscapacidad())
                .tipoIdDiscapacidad(model.getTipoIdDiscapacidad())
                .beneficioProvGalapagos(model.getBeneficioProvGalapagos())
                .enfermedadCatastrofica(model.getEnfermedadCatastrofica())
                .fechaIngreso(Objects.nonNull(model.getFechaIngreso())
                        ? model.getFechaIngreso().toString()
                        : null)
                .estado(model.getEstado())
                .pais(builderPaisResponse(model.getPais()))
                .estado(model.getEstado())
                .sueldoBase(model.getSueldoBase())
                .fechaNacimiento(Objects.nonNull(model.getFechaNacimiento())
                        ? DateUtils.toString(model.getFechaNacimiento()) : null)
                .fechaSalida(Objects.nonNull(model.getFechaSalida())
                        ? DateUtils.toString(model.getFechaSalida()) : null)
                .idCargo(model.getIdCargo())
                .apellidos(model.getApellidos())
                .nombres(model.getNombres())
                .build();
    }

    private TbPaisEntity builderPais(String codigoPais) {
        return TbPaisEntity.builder()
                .codigoPais(codigoPais)
                .build();
    }

    private TbPaisGetOneDto builderPaisResponse(TbPaisEntity model) {
        return TbPaisGetOneDto.builder()
                .pais(model.getPais())
                .codigoPais(model.getCodigoPais())
                .build();
    }


}
