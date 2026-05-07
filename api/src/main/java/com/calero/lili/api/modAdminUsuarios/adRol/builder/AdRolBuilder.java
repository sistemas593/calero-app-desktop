package com.calero.lili.api.modAdminUsuarios.adRol.builder;

import com.calero.lili.api.modAdminUsuarios.adRol.AdRolEntity;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.AdRolDtoRequest;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.AdRolDtoResponse;
import org.springframework.stereotype.Component;

@Component
public class AdRolBuilder {

    public AdRolEntity builderEntity(Long idData, Long idEmpresa, AdRolDtoRequest model) {
        return AdRolEntity.builder()
                .idData(idData)
                .idEmpresa(idEmpresa)
                .nombre(model.getNombre())
                .build();
    }

    public AdRolEntity builderUpdate(AdRolDtoRequest model, AdRolEntity item) {
        return AdRolEntity.builder()
                .idRol(item.getIdRol())
                .nombre(model.getNombre())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .build();
    }


    public AdRolDtoResponse builderResponse(AdRolEntity model) {
        return AdRolDtoResponse.builder()
                .idRol(model.getIdRol())
                .nombre(model.getNombre())

                .build();
    }
}


