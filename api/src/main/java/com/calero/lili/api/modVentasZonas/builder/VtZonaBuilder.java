package com.calero.lili.api.modVentasZonas.builder;

import com.calero.lili.api.modVentasZonas.VtZonaEntity;
import com.calero.lili.api.modVentasZonas.dto.VtZonaCreationRequestDto;
import com.calero.lili.api.modVentasZonas.dto.VtZonaGetListDto;
import com.calero.lili.api.modVentasZonas.dto.VtZonaGetOneDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VtZonaBuilder {

    public VtZonaEntity builderEntity(VtZonaCreationRequestDto model, Long idData, Long idEmpresa) {
        return VtZonaEntity.builder()
                .idZona(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .zona(model.getZona())
                .build();
    }

    public VtZonaEntity builderUpdateEntity(VtZonaCreationRequestDto model, VtZonaEntity item) {
        return VtZonaEntity.builder()
                .idZona(item.getIdZona())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .zona(model.getZona())
                .build();
    }

    public VtZonaGetOneDto builderResponse(VtZonaEntity model) {
        return VtZonaGetOneDto.builder()
                .idZona(model.getIdZona())
                .zona(model.getZona())
                .build();
    }

    public VtZonaGetListDto builderListResponse(VtZonaEntity model) {
        return VtZonaGetListDto.builder()
                .idZona(model.getIdZona())
                .zona(model.getZona())
                .build();
    }
}
