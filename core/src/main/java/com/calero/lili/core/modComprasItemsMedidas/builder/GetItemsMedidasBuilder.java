package com.calero.lili.core.modComprasItemsMedidas.builder;

import com.calero.lili.core.modComprasItemsMedidas.GeItemsMedidasEntity;
import com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaCreationRequestDto;
import com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaCreationResponseDto;
import com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaReportDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetItemsMedidasBuilder {

    public GeItemsMedidasEntity builderEntity(GeItemMedidaCreationRequestDto model, Long idData) {
        return GeItemsMedidasEntity.builder()
                .unidadMedida(model.getUnidadMedida())
                .idData(idData)
                .idUnidadMedida(UUID.randomUUID())
                .build();
    }

    public GeItemMedidaCreationResponseDto builderResponse(GeItemsMedidasEntity model) {
        return GeItemMedidaCreationResponseDto.builder()
                .idUnidadMedida(model.getIdUnidadMedida())
                .unidadMedida(model.getUnidadMedida())
                .idUnidadMedida(model.getIdUnidadMedida())
                .build();
    }

    public GeItemsMedidasEntity builderUpdateEntity(GeItemMedidaCreationRequestDto model, GeItemsMedidasEntity item) {
        return GeItemsMedidasEntity.builder()
                .unidadMedida(model.getUnidadMedida())
                .idData(item.getIdData())
                .idUnidadMedida(item.getIdUnidadMedida())
                .build();
    }

    public GeItemMedidaReportDto builderListDto(GeItemsMedidasEntity model){
        return GeItemMedidaReportDto.builder()
                .idUnidadMedida(model.getIdUnidadMedida())
                .unidadMedida(model.getUnidadMedida())
                .build();
    }

}
