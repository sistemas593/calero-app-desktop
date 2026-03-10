package com.calero.lili.api.modComprasItemsCategorias.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class GeItemCategoriaReportDto {

    private UUID idCategoria;
    private String categoria;
    private String nivel;
}
