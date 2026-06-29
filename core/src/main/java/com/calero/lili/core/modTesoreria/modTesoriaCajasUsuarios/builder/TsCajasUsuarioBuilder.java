package com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.builder;

import com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.TsCajasUsuariosEntity;
import com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.dto.TsCajasUsuarioListResponseDto;
import com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.dto.TsCajasUsuarioRequestDto;
import com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.dto.TsCajasUsuarioResponseDto;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class TsCajasUsuarioBuilder {


    public TsCajasUsuariosEntity builderEntity(Long idData, Long iEmpresa, TsCajasUsuarioRequestDto model) {
        return TsCajasUsuariosEntity.builder()
                .idCajaUsuario(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(iEmpresa)
                .idUsuario(model.getIdUsuario())
                .build();
    }

    public TsCajasUsuariosEntity builderUpdateEntity(TsCajasUsuarioRequestDto model, TsCajasUsuariosEntity entidad) {
        return TsCajasUsuariosEntity.builder()
                .idCajaUsuario(entidad.getIdCajaUsuario())
                .idData(entidad.getIdData())
                .idEmpresa(entidad.getIdEmpresa())
                .idUsuario(model.getIdUsuario())
                .build();
    }

    public TsCajasUsuarioResponseDto builderResponse(TsCajasUsuariosEntity model) {
        return TsCajasUsuarioResponseDto.builder()
                .idCajaUsuario(model.getIdCajaUsuario())
                .idUsuario(model.getIdUsuario())
                .idCajas(Objects.nonNull(model.getCajas()) ? model.getCajas().getIdCaja() : null)
                .nombreCaja(Objects.nonNull(model.getCajas()) ? model.getCajas().getNombre() : null)
                .codigoCaja(Objects.nonNull(model.getCajas()) ? model.getCajas().getCodigoCaja() : null)
                .build();
    }

    public TsCajasUsuarioListResponseDto builderListResponse(TsCajasUsuariosEntity model) {
        return TsCajasUsuarioListResponseDto.builder()
                .idCajaUsuario(model.getIdCajaUsuario())
                .idUsuario(model.getIdUsuario())
                .idCajas(Objects.nonNull(model.getCajas()) ? model.getCajas().getIdCaja() : null)
                .nombreCaja(Objects.nonNull(model.getCajas()) ? model.getCajas().getNombre() : null)
                .codigoCaja(Objects.nonNull(model.getCajas()) ? model.getCajas().getCodigoCaja() : null)
                .build();
    }

}
