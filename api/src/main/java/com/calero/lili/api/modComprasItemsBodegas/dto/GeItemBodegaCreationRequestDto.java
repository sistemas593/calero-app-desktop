package com.calero.lili.api.modComprasItemsBodegas.dto;

import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class GeItemBodegaCreationRequestDto {

    private String bodega;
    private String sucursal;

}
