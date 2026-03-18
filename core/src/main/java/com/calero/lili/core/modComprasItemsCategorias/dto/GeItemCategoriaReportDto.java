package com.calero.lili.core.modComprasItemsCategorias.dto;

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
