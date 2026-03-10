package com.calero.lili.api.modAdminUsuarios.adRol.builder;

import com.calero.lili.api.modAdminUsuarios.adGrupos.AdGruposEntity;
import com.calero.lili.api.modAdminUsuarios.adRol.AdRolEntity;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.AdRolDtoRequest;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.AdRolDtoResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdRolBuilder {

    public AdRolEntity builderEntity(AdRolDtoRequest model) {
        return AdRolEntity.builder()
                .nombre(model.getNombre())
                .build();
    }

    public AdRolEntity builderUpdate(AdRolDtoRequest model, AdRolEntity item) {
        return AdRolEntity.builder()
                .idRol(item.getIdRol())
                .nombre(model.getNombre())
                .build();
    }


    public AdRolDtoResponse builderResponse(AdRolEntity model) {
        return AdRolDtoResponse.builder()
                .idRol(model.getIdRol())
                .nombre(model.getNombre())
                .grupos(builderListGrupos(model.getGrupos()))
                .build();
    }

    private List<AdRolDtoResponse.Grupos> builderListGrupos(List<AdGruposEntity> grupos) {
        return grupos.stream()
                .map(this::builderGrupo)
                .toList();
    }

    private AdRolDtoResponse.Grupos builderGrupo(AdGruposEntity adGruposEntity) {
        return AdRolDtoResponse.Grupos.builder()
                .idGrupo(adGruposEntity.getIdGrupo())
                .grupo(adGruposEntity.getNombre())
                .build();
    }

}
