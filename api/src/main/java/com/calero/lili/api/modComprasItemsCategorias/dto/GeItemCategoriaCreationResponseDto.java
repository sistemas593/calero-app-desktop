package com.calero.lili.api.modComprasItemsCategorias.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class GeItemCategoriaCreationResponseDto {
    private UUID idCategoria;
    private String categoria;
    private String nivel;

}
