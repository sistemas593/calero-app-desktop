package com.calero.lili.api.modComprasItemsMarcas.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class GeItemMarcasReportDto {

    private UUID idMarca;
    private String marca;
}
