package com.calero.lili.api.modAdminUsuarios.adPermisos.builder;

import com.calero.lili.api.modAdminUsuarios.adPermisos.AdPermisosEntity;
import com.calero.lili.api.modAdminUsuarios.adPermisos.dto.AdPermisosRequestDto;
import com.calero.lili.api.modAdminUsuarios.adPermisos.dto.AdPermisosResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AdPermisosBuilder {


    public AdPermisosEntity builderEntity(AdPermisosRequestDto model) {
        return AdPermisosEntity.builder()
                .permiso(model.getPermiso())
                .descripcion(model.getDescripcion())
                .build();
    }

    public AdPermisosEntity builderUpdate(AdPermisosRequestDto model, AdPermisosEntity item) {
        return AdPermisosEntity.builder()
                .idPermiso(item.getIdPermiso())
                .permiso(model.getPermiso())
                .descripcion(model.getDescripcion())
                .build();
    }

    public AdPermisosResponseDto builderResponse(AdPermisosEntity model) {
        return AdPermisosResponseDto.builder()
                .idPermiso(model.getIdPermiso())
                .permiso(model.getPermiso())
                .descripcion(model.getDescripcion())
                .build();
    }

}
