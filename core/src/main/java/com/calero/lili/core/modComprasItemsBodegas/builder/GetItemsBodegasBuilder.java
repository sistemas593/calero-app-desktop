package com.calero.lili.core.modComprasItemsBodegas.builder;

import com.calero.lili.core.modComprasItemsBodegas.IvBodegaEntity;
import com.calero.lili.core.modComprasItemsBodegas.dto.GeItemBodegaCreationRequestDto;
import com.calero.lili.core.modComprasItemsBodegas.dto.GeItemBodegaCreationResponseDto;
import com.calero.lili.core.modComprasItemsBodegas.dto.GeItemBodegaReportDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetItemsBodegasBuilder {


    public IvBodegaEntity builderEntity(GeItemBodegaCreationRequestDto model,
                                        Long idData, Long idEmpresa) {
        return IvBodegaEntity.builder()
                .bodega(model.getBodega())
                .sucursal(model.getSucursal())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .idBodega(UUID.randomUUID())
                .build();
    }


    public IvBodegaEntity builderUpdateEntity(GeItemBodegaCreationRequestDto model, IvBodegaEntity item) {
        return IvBodegaEntity.builder()
                .bodega(model.getBodega())
                .sucursal(model.getSucursal())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .idBodega(item.getIdBodega())
                .build();
    }


    public GeItemBodegaCreationResponseDto builderDto(IvBodegaEntity model) {
        return GeItemBodegaCreationResponseDto.builder()
                .idBodega(model.getIdBodega())
                .bodega(model.getBodega())
                .sucursal(model.getSucursal())
                .build();
    }

    public GeItemBodegaReportDto builderReportDto(IvBodegaEntity model){
        return GeItemBodegaReportDto.builder()
                .idBodega(model.getIdBodega())
                .sucursal(model.getSucursal())
                .bodega(model.getBodega())
                .build();
    }
}
