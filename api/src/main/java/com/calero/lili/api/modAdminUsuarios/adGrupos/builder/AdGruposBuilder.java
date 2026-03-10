package com.calero.lili.api.modAdminUsuarios.adGrupos.builder;

import com.calero.lili.api.modAdminUsuarios.adGrupos.AdGruposEntity;
import com.calero.lili.api.modAdminUsuarios.adGrupos.dto.AdGruposRequestDto;
import com.calero.lili.api.modAdminUsuarios.adGrupos.dto.AdGruposResponseDto;
import com.calero.lili.api.modAdminUsuarios.adPermisos.AdPermisosEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdGruposBuilder {

    public AdGruposEntity builderEntity(AdGruposRequestDto model) {
        return AdGruposEntity.builder()
                .nombre(model.getNombre())
                .build();
    }

    public AdGruposEntity builderUpdate(AdGruposRequestDto model, AdGruposEntity item) {
        return AdGruposEntity.builder()
                .idGrupo(item.getIdGrupo())
                .nombre(model.getNombre())
                .build();
    }

    public AdGruposResponseDto builderResponse(AdGruposEntity model) {
        return AdGruposResponseDto.builder()
                .idGrupoPermiso(model.getIdGrupo())
                .nombre(model.getNombre())
                .permisos(builderListPermisos(model.getPermisos()))
                .build();
    }

    private List<AdGruposResponseDto.Permisos> builderListPermisos(List<AdPermisosEntity> permisos) {
        return permisos.stream()
                .map(this::builderPermiso)
                .toList();
    }

    private AdGruposResponseDto.Permisos builderPermiso(AdPermisosEntity model) {
        return AdGruposResponseDto.Permisos.builder()
                .idPermiso(model.getIdPermiso())
                .descripcion(model.getDescripcion())
                .permiso(model.getPermiso())
                .build();
    }
}
