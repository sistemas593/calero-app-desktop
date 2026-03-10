package com.calero.lili.api.modComprasItemsMarcas.builder;

import com.calero.lili.api.modComprasItemsMarcas.GeItemsMarcasEntity;
import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemMarcasReportDto;
import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemMedidaCreationResponseDto;
import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemsMarcasCreationRequestDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetItemsMarcasBuilder {

    public GeItemsMarcasEntity builderEntity(GeItemsMarcasCreationRequestDto model, Long idData) {
        return GeItemsMarcasEntity.builder()
                .marca(model.getMarca())
                .idData(idData)
                .idMarca(UUID.randomUUID())
                .build();
    }

    public GeItemMedidaCreationResponseDto builderResponse(GeItemsMarcasEntity model) {
        return GeItemMedidaCreationResponseDto.builder()
                .idMarca(model.getIdMarca())
                .marca(model.getMarca())
                .idMarca(model.getIdMarca())
                .build();
    }

    public GeItemsMarcasEntity builderUpdateEntity(GeItemsMarcasCreationRequestDto model, GeItemsMarcasEntity item) {
        return GeItemsMarcasEntity.builder()
                .marca(model.getMarca())
                .idData(item.getIdData())
                .idMarca(item.getIdMarca())
                .build();
    }

    public GeItemMarcasReportDto builderListDto(GeItemsMarcasEntity model){
        return GeItemMarcasReportDto.builder()
                .marca(model.getMarca())
                .idMarca(model.getIdMarca())
                .build();
    }

}
