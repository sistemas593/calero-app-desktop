package com.calero.lili.api.modComprasItemsBodegas.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class GeItemBodegaReportDto {
    private UUID idBodega;
    private String bodega;
    private String sucursal;
}
