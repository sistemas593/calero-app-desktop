package com.calero.lili.core.modComprasItemsCategorias.dto;

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
