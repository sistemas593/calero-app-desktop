package com.calero.lili.core.adInfoAdicional.builder;

import com.calero.lili.core.adInfoAdicional.AdInfoAdicionalEntity;
import com.calero.lili.core.adInfoAdicional.dto.AdInfoAdicionalRequestDto;
import com.calero.lili.core.adInfoAdicional.dto.AdInfoAdicionalResponseDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdInfoAdicionalBuilder {

    public AdInfoAdicionalEntity builderEntity(Long idData, Long idEmpresa, AdInfoAdicionalRequestDto model) {
        return AdInfoAdicionalEntity.builder()
                .idInfoAdicional(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .documento(model.getDocumento())
                .informacionAdicional(model.getInformacionAdicional())
                .build();
    }


    public AdInfoAdicionalEntity builderUpdateEntity(AdInfoAdicionalRequestDto model, AdInfoAdicionalEntity item) {
        return AdInfoAdicionalEntity.builder()
                .idInfoAdicional(item.getIdInfoAdicional())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .documento(model.getDocumento())
                .informacionAdicional(model.getInformacionAdicional())
                .build();
    }

    public AdInfoAdicionalResponseDto builderResponse(AdInfoAdicionalEntity model){
        return AdInfoAdicionalResponseDto.builder()
                .idInfoAdicional(model.getIdInfoAdicional())
                .documento(model.getDocumento())
                .informacionAdicional(model.getInformacionAdicional())
                .build();
    }

}
