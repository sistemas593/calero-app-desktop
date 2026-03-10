package com.calero.lili.api.modComprasItemsCategorias.builder;

import com.calero.lili.api.modComprasItemsCategorias.GeItemsCategoriaEntity;
import com.calero.lili.api.modComprasItemsCategorias.dto.GeItemCategoriaCreationResponseDto;
import com.calero.lili.api.modComprasItemsCategorias.dto.GeItemCategoriaReportDto;
import com.calero.lili.api.modComprasItemsCategorias.dto.GeItemsCategoriaCreationRequestDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GeItemsCategoriaBuilder {

    public GeItemsCategoriaEntity builderEntity(GeItemsCategoriaCreationRequestDto model, Long idData) {
        return GeItemsCategoriaEntity.builder()
                .categoria(model.getCategoria())
                .idData(idData)
                .idCategoria(UUID.randomUUID())
                .nivel(model.getNivel())
                .build();
    }

    public GeItemCategoriaCreationResponseDto builderResponse(GeItemsCategoriaEntity model) {
        return GeItemCategoriaCreationResponseDto.builder()
                .idCategoria(model.getIdCategoria())
                .categoria(model.getCategoria())
                .nivel(model.getNivel())
                .idCategoria(model.getIdCategoria())
                .build();
    }

    public GeItemsCategoriaEntity builderUpdateEntity(GeItemsCategoriaCreationRequestDto model, GeItemsCategoriaEntity item) {
        return GeItemsCategoriaEntity.builder()
                .categoria(model.getCategoria())
                .idData(item.getIdData())
                .idCategoria(item.getIdCategoria())
                .nivel(model.getNivel())
                .build();
    }

    public GeItemCategoriaReportDto builderListDto(GeItemsCategoriaEntity model){
        return GeItemCategoriaReportDto.builder()
                .categoria(model.getCategoria())
                .nivel(model.getNivel())
                .idCategoria(model.getIdCategoria())
                .build();
    }

}
