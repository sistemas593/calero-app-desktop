package com.calero.lili.core.modComprasItemsMedidas.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class GeItemMedidaReportDto {
    private UUID idUnidadMedida;
    private String unidadMedida;
}
