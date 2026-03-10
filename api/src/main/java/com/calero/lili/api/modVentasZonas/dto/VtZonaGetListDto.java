package com.calero.lili.api.modVentasZonas.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class VtZonaGetListDto {

    private UUID idZona;

    private String zona;

}
